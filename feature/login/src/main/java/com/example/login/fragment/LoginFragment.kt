package com.example.login.fragment

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.base.BaseFragment
import com.example.lijiemusic.core.navigation.RoutePath
import com.example.login.LoginViewModel
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