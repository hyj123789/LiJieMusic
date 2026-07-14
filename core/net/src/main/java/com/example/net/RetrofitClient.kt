package com.example.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//单例模式保证单一
object RetrofitClient{
    //固定的开头请求
    private const val BASE_URL = "https://music.generalsio.top/"
    private val okHttpClient: OkHttpClient by lazy {
        //日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)   //连接超时时间
            .readTimeout(15, TimeUnit.SECONDS)  //读取超时时间
            .writeTimeout(15, TimeUnit.SECONDS) //写入超时时间
            .addInterceptor(loggingInterceptor) //装上日志拦截器
            .addInterceptor(AuthInterceptor())  //装上自动拦截器好接入Header
            .build()
    }

    //配置Retrofit客服代理
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) //绑定OkHttp
            .addConverterFactory(GsonConverterFactory.create()) //装上Gson转换器
            .build()
    }

    //开放给外面的可以通用的调用网络请求的方法
    fun <T> createApi(apiClass: Class<T>): T {
        return retrofit.create(apiClass)
    }
}