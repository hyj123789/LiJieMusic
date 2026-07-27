package com.example.player.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.player.PlayerViewModel
import com.example.player.R
import com.example.util.ToastUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.getValue

class ShareSongBottomSheet( songId : Long) : BottomSheetDialogFragment() {

    private val viewModel: PlayerViewModel by activityViewModels()

    private val id = songId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_share_song, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etContent = view.findViewById<EditText>(R.id.etShareContent)
        val tvCharCount = view.findViewById<TextView>(R.id.tvCharCount)
        val btnPublish = view.findViewById<Button>(R.id.btnPublish)
        val ivClose = view.findViewById<View>(R.id.ivClose)
        val ivSongCover = view.findViewById<ImageView>(R.id.ivSongCover)
        val tvSongInfo = view.findViewById<TextView>(R.id.tvSongInfo)


        ivClose.setOnClickListener {
            dismiss()
        }

        // 2. 监听输入框字数变化
        etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0
                tvCharCount.text = "$length/140"

                //没有输入文字时，按钮不可点击且半透明
                btnPublish.isEnabled = length > 0
                btnPublish.alpha = if (length > 0) 1.0f else 0.5f
            }
        })

        //初始化按钮状态
        btnPublish.isEnabled = false
        btnPublish.alpha = 0.5f

        //发布按钮逻辑
        btnPublish.setOnClickListener {
            val msg = etContent.text.toString().trim()
            if (msg.isEmpty()) return@setOnClickListener

           viewModel.fetchShareSonger(id,msg)
        }

        //
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    //封面
                    viewModel.coverUrl.collect { url ->
                        //当网络请求成功url有值的时候，这里的代码才会被触发！
                        if (!url.isNullOrEmpty()) {
                            Glide.with(this@ShareSongBottomSheet)
                                .load(url)
                                .into(ivSongCover)
                        } else {
                            Log.d("hyj", "播放器封面链接还是空的！")
                        }
                    }
                }

                // 只需要一个 launch 协程
                launch {
                    // 将 artistName 和 songName 组合起来
                    combine(viewModel.artistName, viewModel.songName) { artist, name ->
                        // 这里将两个值打包成一个 Pair (或者你自定义的数据类) 传递给下游
                        artist to name
                    }.collect { (artist, name) ->
                        tvSongInfo.text = "$name - $artist"
                    }
                }

                launch {
                    try {
                        viewModel.sharecode.collect { code ->
                            when (code) {
                                0 -> {
                                }
                                200 -> {
                                    ToastUtil.popToast("发布成功", requireContext())
                                    viewModel.resetShareCode()
                                    dismiss()
                                }
                                else -> {
                                    // 非 0 且非 200 的状态（比如 400，或者其他网络错误码）
                                    ToastUtil.popToast("发布失败，请稍后再试", requireContext())
                                    // 【关键】报错弹完 Toast 后，也要重置状态，防止疯狂弹！
                                    viewModel.resetShareCode()
                                }
                            }
                        }
                    } catch (e : Exception) {
                        Log.e("hyj","share出现错误",e)
                    }
                }
            }
        }
    }
}