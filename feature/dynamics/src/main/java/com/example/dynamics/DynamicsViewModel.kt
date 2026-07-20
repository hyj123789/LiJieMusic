package com.example.dynamics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamics.model.Event
import com.example.net.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DynamicsViewModel : ViewModel() {
    private val api = RetrofitClient.createApi(DynamicsApi::class.java)
    private val _toastMsg = MutableStateFlow<String?>(null)
    private val _rvList = MutableStateFlow<List<Event>?>(null)
    val toastMsg : StateFlow<String?> = _toastMsg
    val rvList : StateFlow<List<Event>?> = _rvList

    fun init(){
        viewModelScope.launch {
            try {
                val friendsDynamics = api.getFriendsDynamics()
                if(friendsDynamics.code == 200){
                    _toastMsg.value="加载成功，请等待"
                    _rvList.value=friendsDynamics.event
                }
            } catch (e: Exception) {
                Log.d("ljh","动态网络请求失败"+e.message)
                _toastMsg.value="加载失败"
            }
        }
    }
}