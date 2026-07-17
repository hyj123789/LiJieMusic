package com.example.login.fragment

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.base.BaseFragment
import com.example.lijiemusic.core.navigation.RoutePath
import com.example.login.LoginViewModel
import com.example.login.R
import com.example.login.databinding.FragmentLoginBinding
import com.example.util.ToastUtil
import com.therouter.TheRouter
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()
    override fun initEvent() {
        super.initEvent()
        binding.btnLogin.setOnClickListener {
            viewModel.loginByPhone(binding.etPhone.text.toString(),binding.etPassword.text.toString())
        }
        binding.btnCaptcha.setOnClickListener {
            viewModel.sendCaptcha(binding.etPhone.text.toString())
        }
        binding.btnLoginMail.setOnClickListener {
            ToastUtil.popToastLong("邮箱登录暂未开放！",requireContext())
            findNavController().navigate(R.id.action_loginFragment_to_mailFragment)
        }
        binding.btnLoginScan.setOnClickListener {
            ToastUtil.popToast("跳转二维码登录界面中",requireContext())
            findNavController().navigate(R.id.action_loginFragment_to_scanFragment)
        }
        binding.tvGuestLogin.setOnClickListener {
            ToastUtil.popToastLong("游客登录中，稍慢，请等待",requireContext())
            viewModel.loginByGuest()
        }
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.toastMsg.collect { msg ->
                        ToastUtil.popToast(msg,requireContext())
                        Log.d("ljh",msg)
                    }
                }
                launch {
                    viewModel.loginSuccess.collect { bool ->
                        if (bool) {
                            TheRouter.build(RoutePath.MAIN_ACTIVITY).navigation()
                            Log.d("ljh","跳转方法执行了")
                        }
                    }
                }
            }
        }
    }
}