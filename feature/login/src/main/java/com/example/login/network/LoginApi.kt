package com.example.login.network

import com.example.login.network.bean.CheckQrStatusRes
import com.example.login.network.bean.CreateQrRes
import com.example.login.network.bean.GetQrKeyRes
import com.example.login.network.bean.GuestLoginRes
import com.example.login.network.bean.RefreshLoginRes
import com.example.login.network.bean.SendCaptchaRes
import com.example.login.network.bean.loginbyphone.LoginByPhoneRes
import com.example.model.LoginStatusRes
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApi {
    @GET("/captcha/sent")
    suspend fun sentCaptcha(@Query("phone") phone: String): SendCaptchaRes
    @GET("/login/cellphone")
    suspend fun loginByPhone(@Query("phone")phone: String,
                             @Query("password")password: String = "xxx",
                             @Query("captcha") captcha: String) : LoginByPhoneRes
    @GET("/login/qr/key")
    suspend fun getQrKey() : GetQrKeyRes
    @GET("/login/qr/create")
    suspend fun createQr(@Query("key")key: String,
                         @Query("qrimg") qrimg : Boolean = true) : CreateQrRes
    @GET("/login/qr/check")
    suspend fun checkQrStatus(@Query("key") key: String) : CheckQrStatusRes
    @GET("/register/anonimous")
    suspend fun guestLogin(): GuestLoginRes
    @GET("/login/status")
    suspend fun getLoginStatus() : LoginStatusRes
    @GET("/login/refresh")
    suspend fun refreshLoginStatus() : RefreshLoginRes
}