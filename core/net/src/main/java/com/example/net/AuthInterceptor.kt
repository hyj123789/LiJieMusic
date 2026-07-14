package com.example.net

import okhttp3.Interceptor
import okhttp3.Response

//很多api需要加上cookie才可以进行访问
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        //根据文档来看好像是 os=pc
        val cookie = "os=pc"
        //构造一个新的请求，把Cookie塞进Header里
        val requestBuilder = originalRequest.newBuilder()
        if (cookie.isNotEmpty()) {
            requestBuilder.addHeader("Cookie", cookie)
        }
        //继续执行带有Header的新请求
        return chain.proceed(requestBuilder.build())
    }
}