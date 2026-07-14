package com.example.util

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast

object ToastUtil {
    //弹出短Toast
    fun popToast(msg: String, context: Context) {
        show(msg, context, Toast.LENGTH_SHORT)
    }
    //弹出长Toast
    fun popToastLong(msg: String, context: Context) {
        show(msg, context, Toast.LENGTH_LONG)
    }
    //判断SDK版本来确定调用哪种Toast
    private fun show(
        msg: String,
        context: Context,
        duration: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 及以上使用系统 Toast
            Toast.makeText(
                context.applicationContext,
                msg,
                duration
            ).show()
        } else {
            // Android 10 及以下使用自定义 Toast
            showCustomToast(msg, context, duration)
        }
    }

    private fun showCustomToast(
        msg: String,
        context: Context,
        duration: Int
    ) {
        val toastView = LayoutInflater.from(context)
            .inflate(R.layout.toast_custom, null)

        toastView.findViewById<TextView>(
            R.id.tvToastMessage
        ).text = msg

        val enterAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.fade_in
        )

        toastView.startAnimation(enterAnimation)

        Toast(context.applicationContext).apply {
            setGravity(
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,//底部居中的位置
                0,
                dpToPx(context, 80)
            )

            this.duration = duration
            view = toastView
            show()
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt() //把dp转换成对应的像素px=dp×density
        //加0.5f是为了：toInt() 会直接舍弃小数部分，而不是四舍五入。加上0.5f就能实现四舍五入的效果
    }
}
