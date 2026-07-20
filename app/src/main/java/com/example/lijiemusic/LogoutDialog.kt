package com.example.lijiemusic

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.lijiemusic.databinding.DialogLogoutBinding
import com.example.net.CookieManager
import com.example.therouter.RoutePath
import com.therouter.TheRouter
import kotlin.system.exitProcess

class LogoutDialog : DialogFragment() {
    private var _binding: DialogLogoutBinding? =null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLogoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnAccount.setOnClickListener {
                CookieManager.logout(requireContext())
                TheRouter.build(RoutePath.LAUNCH_MAIN).navigation()
            }
            btnApplication.setOnClickListener {
                activity?.finishAffinity()
                exitProcess(0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)//将背景设置成完全透明，避免覆盖卡片布局
    }
}
