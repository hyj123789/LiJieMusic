package com.example.login.fragment

import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.base.BaseFragment
import com.example.login.LoginViewModel
import com.example.login.R
import com.example.login.databinding.FragmentLoginBinding
import com.example.therouter.RoutePath
import com.example.util.ToastUtil
import com.therouter.TheRouter
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()
    val countDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            // 每秒回调一次，更新按钮文字和状态
            binding.btnCaptcha.isEnabled = false
            binding.btnCaptcha.text = "${millisUntilFinished / 1000}s 后重试"
        }

        override fun onFinish() {
            // 倒计时结束，恢复按钮
            binding.btnCaptcha.isEnabled = true
            binding.btnCaptcha.text = "获取验证码"
        }
    }
    //设置点击事件
    override fun initEvent() {
        super.initEvent()
        binding.btnLogin.setOnClickListener {
            viewModel.loginByPhone(binding.etPhone.text.toString(),binding.etPassword.text.toString())
        }
        binding.btnCaptcha.setOnClickListener {
            if (binding.etPhone.text.toString().isBlank()){
                ToastUtil.popToastLong("wochaowei,没写号码你获取什么验证码",requireContext())
                return@setOnClickListener
            }
            viewModel.sendCaptcha(binding.etPhone.text.toString())
            countDownTimer.start()
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

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}