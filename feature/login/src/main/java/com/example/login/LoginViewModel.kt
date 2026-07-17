package com.example.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.net.RetrofitClient
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginSuccess = MutableStateFlow(false)
    private val _toastMsg = MutableStateFlow<String>("")
    private val api = RetrofitClient.createApi(LoginApi::class.java)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess
    val toastMsg = _toastMsg

    fun loginByPhone(phone: String,captcha: String){
        viewModelScope.launch {
            val loginByPhone = api.loginByPhone(phone, "xxx", captcha)
            if(loginByPhone.code==200){
                _toastMsg.value="验证成功，正在登录"
                _loginSuccess.value=true
            }else{
                _toastMsg.value=loginByPhone.msg
            }
        }
    }
    fun sendCaptcha(phone: String){
        viewModelScope.launch {
            api.sentCaptcha(phone)
        }
    }

    fun loginByGuest() {
        viewModelScope.launch {
            val response = api.guestLogin()
            _toastMsg.value="游客登录中~耗时较长~~耐心等待~~~"
        }
    }
}