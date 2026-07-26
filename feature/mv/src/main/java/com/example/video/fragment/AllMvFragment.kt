package com.example.video.fragment

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.base.BaseFragment
import com.example.util.ToastUtil
import com.example.video.VideoViewModel
import com.example.video.adapter.AllMvAdapter
import com.example.video.databinding.FragmentAllMvBinding
import com.example.video.databinding.PopContentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AllMvFragment : BaseFragment<FragmentAllMvBinding>(FragmentAllMvBinding::inflate) {
    private val viewModel: VideoViewModel by viewModels()
    private val mAdapter = AllMvAdapter { id ->
        val request = NavDeepLinkRequest.Builder
            .fromUri(Uri.parse("musicapp://mvPlay/id/$id"))
            .build()
        findNavController().navigate(request)
    }
    private var _popBinding: PopContentBinding? = null
    private val popBinding get() = _popBinding!!
    override fun initView() {
        super.initView()
        binding.rvMvAll.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = mAdapter
        }
    }

    override fun initEvent() {
        super.initEvent()
        _popBinding = PopContentBinding.inflate(LayoutInflater.from(requireContext()))
        val popupWindow = PopupWindow(
            popBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popBinding.btnTypeAll.isSelected = true
        popBinding.btnAreaAll.isSelected = true
        popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        binding.tvFilter.setOnClickListener {
            popupWindow.showAsDropDown(binding.tvFilter)
        }
        popBinding.apply {
            val areaButtons =
                listOf(btnAreaAll, btnAreaCn, btnAreaHt, btnAreaWestern, btnAreaJapan, btnAreaKorea)
            val typeButtons =
                listOf(btnTypeAll, btnTypeOfficial, btnTypeOrigin, btnTypeLive, btnTypeNetEase)
            for (areaButton in areaButtons) {
                areaButton.setOnClickListener {
                    setButtonSelected(areaButtons, areaButton)
                    viewModel.updateAllArea(areaButton.text.toString())
                }

            }
            for (typeButton in typeButtons) {
                typeButton.setOnClickListener {
                    setButtonSelected(typeButtons, typeButton)
                    viewModel.updateAllType(typeButton.text.toString())
                }
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.toastMsg.collect { msg ->
                        if (msg.isNullOrEmpty()) {
                            return@collect
                        }
                        ToastUtil.popToast(msg, requireContext())
                    }
                }
                launch {
                    viewModel.allMvPagingFlow.collectLatest { pagingData ->
                        mAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    /** 只有一个button能被选中 */
    private fun setButtonSelected(buttons: List<Button>, button: Button) {
        for (btn in buttons) {
            btn.isSelected = false
        }
        button.isSelected = true
    }

    override fun onDestroyView() {
        _binding?.rvMvAll?.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _popBinding = null
    }
}