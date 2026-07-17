package com.example.login

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lijiemusic.core.navigation.RoutePath
import com.example.login.model.GetQrKeyRes
import com.example.net.CookieManager
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.net.RetrofitClient
import com.therouter.TheRouter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginSuccess = MutableStateFlow(false)
    private val _toastMsg = MutableStateFlow<String?>(null)
    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    private val _codeStatus = MutableStateFlow<String?>(null)
    private val api = RetrofitClient.createApi(LoginApi::class.java)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess
    val toastMsg: StateFlow<String?> = _toastMsg
    val qrBitmap : StateFlow<Bitmap?> =  _qrBitmap
    val codeStatus: StateFlow<String?> = _codeStatus

    fun loginByPhone(phone: String,captcha: String){
        viewModelScope.launch {
            try{
                val loginByPhone = api.loginByPhone(phone, "xxx", captcha)
                if (loginByPhone.code == 200) {
                    _toastMsg.value = "验证成功，正在登录"
                    val cookie = loginByPhone.cookie
                    val musicU = extractMusicU(cookie)
                    if(musicU==null) return@launch
                    CookieManager.injectCookie(musicU)
                    _loginSuccess.value = true
                } else {
                    _toastMsg.value = loginByPhone.msg
                }
            } catch (e: Exception){
                Log.e("ljh","手机验证出错"+e.message)
            }
        }
    }
    fun sendCaptcha(phone: String){
        viewModelScope.launch {
            try {
                api.sentCaptcha(phone)
            } catch (e: Exception) {
                Log.e("ljh","获取验证码失败"+e.message)
            }
        }
    }

    fun loginByGuest() {
        viewModelScope.launch {
            try {
                val response = api.guestLogin()
            } catch (e: Exception) {
                Log.e("ljh","游客登录失败"+e.message)
                return@launch
            }
            _toastMsg.value="游客登录中~耗时较长~~耐心等待~~~"
            TheRouter.build(RoutePath.MAIN_ACTIVITY).navigation()
        }
    }

    fun loginByScanInPhone() {
        _toastMsg.value="跳转二维码登录中~~~"
    }
    fun loginByMail(){
        _toastMsg.value="邮箱登录暂未开放哇~~~"
    }

    fun getQrCode() {
        viewModelScope.launch {
            var qrKey: GetQrKeyRes? =null
            try{
                qrKey = api.getQrKey()
                if (qrKey.code != 200) {
                    _toastMsg.value = "获取二维码失败，检查网络问题"
                    return@launch
                }
                val createQr = api.createQr(qrKey.data.unikey)
                if (createQr.code != 200) {
                    _toastMsg.value = "获取二维码失败，检查网络问题"
                    return@launch
                }
                val base64Data: String = createQr.data.qrimg
                val pureBase64 = base64Data.substringAfter(",")
                val imageBytes = android.util.Base64.decode(pureBase64, android.util.Base64.DEFAULT)
                val bitmap =
                    android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                _qrBitmap.value = bitmap
            } catch (e: Exception){
                Log.e("ljh","获取二维码失败"+e.message)
            }

            try{
                while (true) {
                    var checkQrStatus = api.checkQrStatus(qrKey!!.data.unikey)
                    _codeStatus.value = checkQrStatus.message
                    Log.d("ljh", "刷新二维码状态")
                    if(checkQrStatus.code == 803) break
                    delay(1000)
                }
            }catch (e: Exception){
                Log.e("ljh","检查二维码状态出现问题"+e.message)
            }
        }
    }
    fun extractMusicU(cookieString: String): String? {
        // 匹配完整的 MUSIC_U=xxx（带上前缀名），传给 Cookie.parse 才能识别
        val regex = Regex("MUSIC_U=[^;]+")
        return regex.find(cookieString)?.value
    }
}