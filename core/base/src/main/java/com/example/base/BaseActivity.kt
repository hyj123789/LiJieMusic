package com.example.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding>(
    private val inflate : (LayoutInflater)->VB
) : AppCompatActivity(){
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = inflate(layoutInflater)
        setContentView(binding.root)

        setupImmersiveStatusBar()

        initView()
        initEvent()
        initObservers()
    }

    open fun initView(){}
    open fun initEvent(){}

    open fun initObservers() {}

    private fun setupImmersiveStatusBar() {
        val window = this.window
        val decorView = window.decorView

        // 这是 Android 做了兼容的 Compat 包
        // 注意，使用了下面这个方法后，状态栏不会再有东西占位，
        // 可以给根布局加上 android:fitsSystemWindows=true
        // 不同布局该属性效果不同，请给合适的布局添加
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, decorView)
        // 如果你要白色的状态栏字体，请在你直接的 Activity 中单独设置 isAppearanceLightStatusBars，这里不提供方法
        //windowInsetsController.isAppearanceLightStatusBars = isDaytimeMode()
        window.statusBarColor = Color.TRANSPARENT //把状态栏颜色设置成透明
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