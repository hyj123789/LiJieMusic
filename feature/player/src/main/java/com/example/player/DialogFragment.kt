package com.example.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.player.databinding.DialogQualityBottomSheetBinding

class QualityBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogQualityBottomSheetBinding? = null
    private val binding get() = _binding!!

    //定义一个回调接口告诉大播放器用户选了啥
    var onQualitySelected: ((qualityLevel: String, qualityName: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogQualityBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemBiaozhun.setOnClickListener {
            binding.biaozhun.setImageResource(R.drawable.check)
            view.postDelayed({
                onQualitySelected?.invoke("standard", "臻品母带")
                dismiss()
            }, 500)
        }

        binding.itemHigh.setOnClickListener {
            binding.high.setImageResource(R.drawable.check)
            view.postDelayed({
                onQualitySelected?.invoke("higher", "臻品全景音")
                dismiss()
            }, 500)

        }

        binding.itemWushun.setOnClickListener {
            binding.wushun.setImageResource(R.drawable.check)
            onQualitySelected?.invoke("exhigh", "臻品音质")
            view.postDelayed({
                onQualitySelected?.invoke("exhigh", "臻品音质")
                dismiss()
            }, 500)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}