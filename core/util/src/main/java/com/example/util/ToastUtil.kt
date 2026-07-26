package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast

object ToastUtil {

    /** 当前正在显示的 Toast 引用，用于取消 */
    private var currentToast: Toast? = null

    /** 主线程 Handler，确保 show() 在主线程调用 */
    private val mainHandler = Handler(Looper.getMainLooper())

    //弹出短Toast
    fun popToast(msg: String, context: Context) {
        show(msg, context, Toast.LENGTH_SHORT)
    }

    //弹出长Toast
    fun popToastLong(msg: String, context: Context) {
        show(msg, context, Toast.LENGTH_LONG)
    }

    /** 取消当前正在显示/等待中的 Toast */
    fun cancelToast() {
        currentToast?.cancel()
        currentToast = null
    }

    //判断SDK版本来确定调用哪种Toast
    private fun show(
        msg: String,
        context: Context,
        duration: Int
    ) {
        // Application Context 防止 Activity 泄漏
        val appCtx = context.applicationContext

        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { showInternal(msg, appCtx, duration) }
        } else {
            showInternal(msg, appCtx, duration)
        }
    }

    private fun showInternal(
        msg: String,
        appCtx: Context,
        duration: Int
    ) {
        // 取消前一个 Toast，避免排队堆积
        currentToast?.cancel()

        val toast = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 及以上使用系统 Toast
            Toast.makeText(appCtx, msg, duration)
        } else {
            // Android 10 及以下使用自定义 Toast
            createCustomToast(msg, appCtx, duration)
        }

        currentToast = toast
        toast.show()
    }

    @SuppressLint("InflateParams")
    private fun createCustomToast(
        msg: String,
        appCtx: Context,
        duration: Int
    ): Toast {
        // 使用 Application Context inflate，避免 Activity 泄漏
        val toastView = LayoutInflater.from(appCtx)
            .inflate(R.layout.toast_custom, null)

        toastView.findViewById<TextView>(R.id.tvToastMessage).text = msg

        val enterAnimation = AnimationUtils.loadAnimation(appCtx, R.anim.fade_in)
        toastView.startAnimation(enterAnimation)

        return Toast(appCtx).apply {
            setGravity(
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                dpToPx(appCtx, 80)
            )
            this.duration = duration
            view = toastView
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }
}
