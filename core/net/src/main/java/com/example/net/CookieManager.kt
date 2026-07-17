package com.example.net

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

object CookieManager : CookieJar{
    private const val BASE_HOST = "music.generalsio.top"
    private val cookieStore = mutableMapOf<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        // 服务器返回 Set-Cookie 时，OkHttp 自动调这个方法
        cookieStore[url.host] = cookies
        Log.d("ljh", "CookieJar.saveFromResponse: host=${url.host}, cookies=$cookies")
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        // 每次请求时，OkHttp 自动调这个拿 cookie
        val cookies = cookieStore[url.host] ?: emptyList()
        Log.d("ljh", "CookieJar.loadForRequest: host=${url.host}, 取出cookies=$cookies")
        return cookies
    }

    // 登录成功后手动注入 JSON 里的 cookie 字符串
    fun injectCookie(cookieStr: String) {
        val url = "https://$BASE_HOST".toHttpUrl()
        val cookie = Cookie.parse(url, cookieStr) ?: run {
            Log.e("ljh", "CookieManager.injectCookie: Cookie.parse失败，格式不对: $cookieStr")
            return
        }
        cookieStore[BASE_HOST] = listOf(cookie)
        Log.d("ljh", "CookieManager.injectCookie: 成功注入 name=${cookie.name}, value=${cookie.value}")
    }
}