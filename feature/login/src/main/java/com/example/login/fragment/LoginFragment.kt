package com.example.login.fragment

import android.content.Context
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
            findNavController().navigate(R.id.action_loginFragment_to_mailFragment)
        }
        binding.btnLoginScan.setOnClickListener {
            viewModel.loginByScanInPhone()
            findNavController().navigate(R.id.action_loginFragment_to_scanFragment)
        }
        binding.tvGuestLogin.setOnClickListener {
            viewModel.loginByGuest()
        }
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.toastMsg.collect { msg ->
                        if (msg==null) return@collect
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