package com.example.lijiemusic

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.base.BaseActivity
import com.example.lijiemusic.databinding.ActivityLaunchBinding
import com.example.login.LoginApi
import com.example.model.UserManager
import com.example.net.CookieManager
import com.example.net.RetrofitClient
import com.example.therouter.RoutePath
import com.example.util.ToastUtil
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.launch
@Route(path = RoutePath.LAUNCH_MAIN)
class LaunchActivity : BaseActivity<ActivityLaunchBinding>(ActivityLaunchBinding::inflate) {
    private val api = RetrofitClient.createApi(LoginApi::class.java)

    override fun initView() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }

    override fun initEvent() {
        super.initEvent()
        if (CookieManager.hasCookie()){
            lifecycleScope.launch {
                try {
                    ToastUtil.popToast("获取登录状态中",this@LaunchActivity)
                    val loginStatus = api.getLoginStatus()
                    if (loginStatus.data.code == 200) {
                        UserManager.account.value = loginStatus.data.account
                        UserManager.profile.value = loginStatus.data.profile
                        ToastUtil.popToastLong("正在登录,跳转主页中",this@LaunchActivity)
                    } else{
                        ToastUtil.popToast("登录过期，请重新登录",this@LaunchActivity)
                    }
                    // 刷新 cookie
                    try {
                        val refresh = api.refreshLoginStatus()
                        val musicU = extractMusicU(refresh.cookie)
                        if (musicU != null) {
                            CookieManager.injectCookie(musicU)
                        }
                    } catch (_: Exception) {
                        Log.d("ljh", "cookie刷新失败，下次启动再试")
                    }
                } catch (e: Exception) {
                    Log.d("ljh", "我了个雷，初始化出问题了捏 ${e.message}")
                } finally {
                    // 无论成功失败都跳主页，登录页有 cookie 会过滤
                    TheRouter.build(RoutePath.MAIN_ACTIVITY).navigation()
                }
            }
        } else {
            // 无 cookie 不走任何跳转，保留 nav_login 登录导航图
        }
    }
    fun extractMusicU(cookieString: String): String? {
        // 匹配完整的 MUSIC_U=xxx（带上前缀名），传给 Cookie.parse 才能识别
        val regex = Regex("MUSIC_U=[^;]+")
        return regex.find(cookieString)?.value
    }
}