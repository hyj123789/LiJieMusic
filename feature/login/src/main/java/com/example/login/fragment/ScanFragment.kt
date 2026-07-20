package com.example.login.fragment

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.base.BaseFragment
import com.example.login.LoginViewModel
import com.example.login.databinding.FragmentScanBinding
import com.example.therouter.RoutePath
import com.therouter.TheRouter
import kotlinx.coroutines.launch

class ScanFragment : BaseFragment<FragmentScanBinding>(FragmentScanBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()
    override fun initEvent() {
        super.initEvent()
        binding.btnFlush.setOnClickListener {
            viewModel.getQrCode()
        }
    }

    override fun initObservers() {
        super.initObservers()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.loginSuccess.collect { bool ->
                        if (bool){
                            TheRouter.build(RoutePath.MAIN_ACTIVITY).navigation()
                            Log.d("ljh","跳转方法执行了")
                        }
                    }
                }
                launch {
                    viewModel.qrBitmap.collect {bitmap->
                        if (bitmap==null) return@collect
                        binding.ivCode.setImageBitmap(bitmap)
                        Log.d("ljh","哟吼吼吼，加载成功")
                    }
                }
                launch {
                    viewModel.codeStatus.collect { status->
                        binding.tvCodeStatus.text=status
                    }
                }
                launch {
                    viewModel.loginSuccess.collect { bool ->
                        if (bool){
                            TheRouter.build(RoutePath.MAIN_ACTIVITY).navigation()
                        }
                    }
                }
            }
        }
    }
}