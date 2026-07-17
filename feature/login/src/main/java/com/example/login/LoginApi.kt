package com.example.login

import com.example.login.model.CheckQrStatusRes
import com.example.login.model.CreateQrRes
import com.example.login.model.GetQrKeyRes
import com.example.login.model.GuestLoginRes
import com.example.login.model.SendCaptchaRes
import com.example.login.model.loginbyphone.LoginByPhoneRes
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApi {
    @GET("/captcha/sent")
    suspend fun sentCaptcha(@Query("phone") phone: String): SendCaptchaRes
    @GET("/login/cellphone")
    suspend fun loginByPhone(@Query("phone")phone: String,
                     @Query("password")password: String = "xxx",
                     @Query("captcha") captcha: String) : LoginByPhoneRes
    @GET("/login")
    suspend fun loginByEmail(@Query("email") email: String,
                     @Query("password")password: String)
    @GET("/login/qr/key")
    suspend fun getQrKey() : GetQrKeyRes
    @GET("/login/qr/create")
    suspend fun createQr(@Query("key")key: String,
                         @Query("qrimg") qrimg : Boolean = true) : CreateQrRes
    @GET("/login/qr/check")
    suspend fun checkQrStatus(@Query("key") key: String) : CheckQrStatusRes
    @GET("/register/anonimous")
    suspend fun guestLogin(): GuestLoginRes
}