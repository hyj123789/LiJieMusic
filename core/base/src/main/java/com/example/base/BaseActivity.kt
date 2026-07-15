package com.example.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding>(
    private val inflate : (LayoutInflater)->VB
) : AppCompatActivity(){
    //延迟赋值，activity销毁的时候会一起销毁不用担心内存泄露
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //初始化 ViewBinding
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        //开启沉浸式状态栏 (让内容延伸到状态栏底部)
        setupImmersiveStatusBar()

        //执行子类的初始化
        initView()
        initEvent()
        initObservers()
    }

    open fun initView(){}
    open fun initEvent(){}

    open fun initObservers() {}

    private fun setupImmersiveStatusBar(){
        // 让内容顶到状态栏下面
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 将状态栏背景设为透明
        window.statusBarColor = Color.TRANSPARENT
        // 默认状态栏字体为黑色 (如果是深色模式可以写逻辑切成白色)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
    }

    @SuppressLint("ServiceCast")
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v != null) {
                // 如果焦点在输入框上，但用户点到了外面，就隐藏键盘
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}