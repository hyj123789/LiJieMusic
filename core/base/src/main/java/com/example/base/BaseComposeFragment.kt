package com.example.base

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment

/**
 * Compose 页面的基类。
 *
 * 与 [BaseFragment]（ViewBinding 体系）并行存在，后续模块迁移到 Compose 时只需：
 * 1. 继承此类而非 BaseFragment
 * 2. 实现 [Content] 提供 Composable UI（内部用各模块自己的主题包装）
 *
 * 导航、activity 操作等副作用通过回调参数传给 Screen，保持 Screen 纯 UI 可复用。
 */
abstract class BaseComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): ComposeView = ComposeView(requireContext()).apply {
        // 随 Fragment view 生命周期自动销毁 Compose 内容，避免内存泄漏
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
        )
        setContent {
            this@BaseComposeFragment.Content()
        }
    }

    @Composable
    abstract fun Content()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initObservers()
    }

    open fun initEvent() {}
    open fun initObservers() {}
}
