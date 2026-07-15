package com.example.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
open class BaseViewModel : ViewModel() {

    //SharedFlow 的好处是“阅后即焚”，横竖屏切换时不会像 LiveData 那样重复弹 Toast (数据倒灌)
    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> get() = _errorFlow

    //核心魔法：全局协程异常捕获器
    // 只要是用下面 launchRequest 包裹的网络请求，报错了绝对不会闪退，全被这里拦截！
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            // 把捕获到的错误信息，发送给 UI 层
            val errorMsg = throwable.message ?: "未知网络错误"
            _errorFlow.emit(errorMsg)
        }
    }

    /**
     * 🚀 终极网络请求封装模板
     * 子类 ViewModel 以后发起请求，全部用这个方法包裹！
     */
    protected fun launchRequest(block: suspend () -> Unit) {
        // 把 exceptionHandler 塞进协程里，如果 block() 里面抛出异常，就会被自动捕获
        viewModelScope.launch(exceptionHandler) {
            block.invoke()
        }
    }
}