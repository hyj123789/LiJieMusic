package com.example.base

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.util.ToastUtil
import kotlinx.coroutines.launch

//base类一个fragmnet减少冗余代码

abstract class BaseFragment<VB : ViewBinding>(
        private val inflate : (LayoutInflater, ViewGroup?, Boolean)->VB
) : Fragment(){

        //初始化的时候默认置空
        private var _binding :VB? = null
        //对外一定不为空
        //暴露给子类使用的binding子类直接调用binding.xxx即可
        protected val binding get() = _binding!!

        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {
                _binding = inflate(inflater, container, false)
                return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
            //调用初始化的两个重写函数
            initView()
            initEvent()
            initObservers()
        }

       //子类需要实现的方法，去初始化ui
        open fun initView() {}
        open fun initEvent() {}

       //选择性重写这个方法，用这个去监听ViewModel的数据的变化
        open fun initObservers() {}

        override fun onDestroyView() {
                super.onDestroyView()
                //销毁的时候内部置空，防止内存泄露
                _binding = null
        }

    protected fun handleApiError(viewModel: BaseViewModel) {
        //Fragment中使用协程监听Flow
        viewLifecycleOwner.lifecycleScope.launch {
            //页面可见阶段开始进行监听
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorFlow.collect { errorMsg ->
                    //调用Li写的ToastUtil
                    //应用全局上下文，以免使用fragment上下文报错
                    context?.applicationContext?.let { ctx ->
                        ToastUtil.popToast(errorMsg, ctx)
                    }
                }
            }
        }
    }
}