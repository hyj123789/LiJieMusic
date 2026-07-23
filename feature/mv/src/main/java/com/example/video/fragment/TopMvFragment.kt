package com.example.video.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.BaseFragment
import com.example.video.VideoViewModel
import com.example.video.adapter.TopMvAdapter
import com.example.video.databinding.FragmentTopMvBinding
import com.example.video.databinding.PopContentBinding
import kotlinx.coroutines.launch
import kotlin.getValue

class TopMvFragment : BaseFragment<FragmentTopMvBinding>(FragmentTopMvBinding::inflate) {
    private val viewModel: VideoViewModel by viewModels()
    private val mAdapter = TopMvAdapter()
    private var _popBinding: PopContentBinding? = null
    private val popBinding get() = _popBinding!!

    override fun initView() {
        super.initView()
        binding.rvMvTop.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d("hhh", "执行初始化捏")
        viewModel.fetchTopMv()
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
        popBinding.cvType.visibility = View.GONE
        popBinding.btnAreaAll.isSelected = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvTopFilter.setOnClickListener {
            popupWindow.showAsDropDown(binding.tvTopFilter)
        }
        popBinding.apply {
            val areaButtons =
                listOf(btnAreaAll, btnAreaCn, btnAreaHt, btnAreaWestern, btnAreaJapan, btnAreaKorea)
            for (areaButton in areaButtons) {
                areaButton.setOnClickListener {
                    setButtonSelected(areaButtons, areaButton)
                    viewModel.updateTopArea(areaButton.text.toString())
                    viewModel.fetchTopMv()
                    binding.tvTopFilter.text = areaButton.text
                }
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.topMvList.collect { list ->
                        if (list.isNullOrEmpty()) return@collect
                        val newList = list.toMutableList()
                        mAdapter.submitList(newList)
                    }
                }
            }
        }
    }

    private fun setButtonSelected(buttons: List<Button>, button: Button) {
        for (btn in buttons) {
            btn.isSelected = false
        }
        button.isSelected = true
    }
}