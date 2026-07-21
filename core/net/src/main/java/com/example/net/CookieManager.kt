package com.example.net

import android.content.Context
import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

object CookieManager : CookieJar {
    private const val BASE_HOST = "music.generalsio.top"
    const val SP_NAME = "cookie_store"
    const val KEY = "raw_cookie"

    private val cookieStore = mutableMapOf<String, List<Cookie>>()
    private lateinit var sp: android.content.SharedPreferences
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val raw = sp.getString(KEY, null)
        if (raw != null) {
            injectCookie(raw)
            Log.d("ljh", "CookieManager: 从SP恢复了cookie")
        }
    }
    fun logout(context: Context){
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        sp.edit().remove(KEY).apply()
        cookieStore.clear()
    }


    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val existing = cookieStore[url.host]?.toMutableList() ?: mutableListOf()
        for (newCookie in cookies) {
            existing.removeAll { it.name == newCookie.name }
            existing.add(newCookie)
        }
        cookieStore[url.host] = existing
        Log.d("ljh", "CookieJar.saveFromResponse: host=${url.host}, 合并后共${existing.size}个cookie")
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore[url.host] ?: emptyList()
        Log.d("ljh", "CookieJar.loadForRequest: host=${url.host}, 取出cookies=$cookies")
        return cookies
    }

    fun hasCookie(): Boolean = cookieStore[BASE_HOST]?.isNotEmpty() == true

    fun injectCookie(cookieStr: String) {
        val url = "https://$BASE_HOST".toHttpUrl()
        val cookie = Cookie.parse(url, cookieStr) ?: run {
            Log.e("ljh", "CookieManager.injectCookie: Cookie.parse失败，格式不对: $cookieStr")
            return
        }
        // 合并到已有 cookie 列表，而不是替换
        val existing = cookieStore[BASE_HOST]?.toMutableList() ?: mutableListOf()
        existing.removeAll { it.name == cookie.name }
        existing.add(cookie)
        cookieStore[BASE_HOST] = existing
        Log.d("ljh", "CookieManager.injectCookie: 成功注入 name=${cookie.name}, value=${cookie.value.take(30)}..., 共${existing.size}个cookie")
        if (::sp.isInitialized) {
            sp.edit().putString(KEY, cookieStr).apply()
        }
    }
}
