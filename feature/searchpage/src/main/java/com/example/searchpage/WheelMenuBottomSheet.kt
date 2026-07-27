package com.example.searchpage

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.searchpage.adapter.AlbumAdapter
import com.example.searchpage.adapter.ArtistAdapter
import com.example.searchpage.adapter.GenreAdapter
import com.example.searchpage.adapter.RadioAdapter
import com.example.searchpage.adapter.TopListAdapter
import com.example.searchpage.adapter.WheelCategoryAdapter
import com.example.util.ToastUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.Long
import kotlin.getValue

class WheelMenuBottomSheet(id: Int) : BottomSheetDialogFragment() {

    private val clickid = id

    private val viewModel: SearchPageViewmodel by viewModels()

    private val categories = listOf("排行榜", "歌手", "曲风", "新歌新碟", "随心听")
    private lateinit var adapter: WheelCategoryAdapter

    private val topListAdapter = TopListAdapter { clickedItem ->
        ToastUtil.popToast("还未开放，敬请期待",requireContext())
        Log.d("TopList", "你点击了: ${clickedItem.name}, ID: ${clickedItem.id}")
    }
     private val artistAdapter = ArtistAdapter { artist ->
         ToastUtil.popToast("还未开放，敬请期待",requireContext())
         Log.d("Click", "点击了歌手: ${artist.name}")
     }

    private val genreAdapter= GenreAdapter { genre ->
        ToastUtil.popToast("还未开放，敬请期待",requireContext())
        Log.d("Click", "点击了曲风: ${genre.tagName}")
    }

    private val albumAdapter = AlbumAdapter { album ->
        ToastUtil.popToast("还未开放，敬请期待",requireContext())
        Log.d("Click", "点击了专辑: ${album.name}")
    }

    private val radioAdapter = RadioAdapter { radio ->
        ToastUtil.popToast("还未开放，敬请期待",requireContext())
        Log.d("Click", "点击了专辑: ${radio.name}")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            //获取BottomSheet的根 View
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                //强制让它的高度等于全屏
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                //按照新的全屏尺寸重新绘制
                it.requestLayout()
                //获取 Behavior 并设置为展开状态
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                //设为 true 防止用户向下滑动时卡在半截，跳过折叠状态
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_wheel_menu, container, false)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val rvWheel = view.findViewById<RecyclerView>(R.id.rvWheel)
        val tvSelectedTitle = view.findViewById<TextView>(R.id.tvSelectedTitle)
        val rvContent = view.findViewById<RecyclerView>(R.id.rvContent)
        val  ivClose = view.findViewById<ImageView>(R.id.ivClose)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvWheel.layoutManager = layoutManager

        //配置顶部的滚轮
        adapter = WheelCategoryAdapter()
        rvWheel.adapter = adapter
        adapter.submitList(categories)

        //配置内容的Rv
        rvContent.layoutManager = LinearLayoutManager(requireContext())


        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvWheel)

        //获屏幕长度
        val screenWidth = resources.displayMetrics.widthPixels
        //获取item长度
        val itemWidthPx = (100 * resources.displayMetrics.density).toInt()
        //获取需要的空白
        val padding = (screenWidth - itemWidthPx) / 2
        //设置边距空白
        rvWheel.setPadding(padding, 0, padding, 0)
        //允许在 Padding 区域绘制内容和滚动
        rvWheel.clipToPadding = false
        //告诉弹窗不需要管上面的rv‘管下面的
        rvWheel.isNestedScrollingEnabled = false
        //滚轮的滑动监听
        rvWheel.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val centerView = snapHelper.findSnapView(layoutManager)
                if (centerView != null) {
                    val position = layoutManager.getPosition(centerView)
                    if (position in categories.indices) {
                        tvSelectedTitle.text = categories[position]
                        adapter.updateSelectedPosition(position)
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //当滑动完全停止 (IDLE) 时触发
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(layoutManager)
                    if (centerView != null) {
                        val position = layoutManager.getPosition(centerView)
                        if (position in categories.indices) {
                            val currentCategory = categories[position]
                            //轮盘停稳了根据选中的分类去请求对应的数据
                            fetchDataForCategory(currentCategory)
                        }
                    }
                }
            }
        })

        //处理rv的滑动冲突
        rvContent.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    //判断 RecyclerView 能不能继续向上或向下滚动
                    //canScrollVertically(1) 检查是否能向上滚动（查看底部内容）
                    //canScrollVertically(-1) 检查是否能向下滚动（查看顶部内容）
                    val canScrollUp = view.canScrollVertically(1)
                    val canScrollDown = view.canScrollVertically(-1)

                    if (canScrollUp || canScrollDown) {
                        //如果列表本身还有空间可以滑，坚决不让父布局插手
                        view.parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        //如果列表已经滑到最顶或最底了，就把控制权还给父布局
                        //这样用户就能顺滑地拖拽整个 BottomSheet 了
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        //返回
        ivClose.setOnClickListener {
            dismiss()
        }
        //加载点击要求的
        fetchDataForCategory(categories[clickid])
        rvWheel.smoothScrollToPosition(clickid)
        initObverse()
    }

    private fun fetchDataForCategory(category: String) {
        val rvContent = requireView().findViewById<RecyclerView>(R.id.rvContent)
        when (category) {
            "排行榜" -> {
                viewModel.fetchTopList()
                rvContent.adapter = topListAdapter
            }
            "歌手" -> {
                rvContent.adapter = artistAdapter
                viewModel.fetchArtist()
            }
            "曲风" -> {
                rvContent.adapter = genreAdapter
                viewModel.fetchGenre()
            }
            "新歌新碟" ->{
                rvContent.adapter = albumAdapter
                viewModel.fetchAlbum()
            }
            "随心听" ->{
                rvContent.adapter = radioAdapter
                viewModel.fetchRadio()
            }
        }

    }

    fun initObverse(){
        viewLifecycleOwner.lifecycleScope.launch {
            //只有在页面可见时才监听，不可见就没有必要监听
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.topList
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            topListAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)

                viewModel.artist
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            artistAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)

                viewModel.genre
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            genreAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)

                viewModel.album
                    .onEach {realData ->
                        if (realData.isNotEmpty()) {
                            albumAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)

                viewModel.radioFlow
                    .onEach {realData ->
                        if (realData.isNotEmpty()) {
                            radioAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)


            }
        }
    }
}