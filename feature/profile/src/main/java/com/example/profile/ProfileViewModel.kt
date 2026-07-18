package com.example.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.UserManager
import com.example.net.RetrofitClient
import com.example.profile.model.playlist.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel(){
    private val _listData = MutableStateFlow<List<Playlist>?>(null)
    val listData : StateFlow<List<Playlist>?> = _listData
    private val api = RetrofitClient.createApi(ProfileApi::class.java)
    fun loadPlaylist(){
        viewModelScope.launch {
            try {
                val playList = api.getPlayList(UserManager.profile.value?.userId.toString())
                _listData.value = playList.playlist
            } catch (e: Exception) {
                Log.e("ljh","获取歌单时捕捉到异常"+e.message)
            }
        }
    }
}