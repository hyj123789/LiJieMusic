package com.example.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.net.RetrofitClient
import com.example.playlist.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel: ViewModel() {
    private val api = RetrofitClient.createApi(PlayListApi::class.java)
    private val _idSong = MutableStateFlow<String?>(null)
    private val _rvList = MutableStateFlow<List<Track>?>(null)
    private val _coverUrl = MutableStateFlow<String?>(null)
    private val _name = MutableStateFlow<String?>(null)
    private val _songCounts = MutableStateFlow<String?>(null)

    val idSong : StateFlow<String?> = _idSong
    val rvList : StateFlow<List<Track>?> = _rvList
    val coverUrl : StateFlow<String?> = _coverUrl
    val name : StateFlow<String?> = _name
    val songCounts : StateFlow<String?> = _songCounts
    fun init(id: String){
        viewModelScope.launch {
            try {
                val playlistRes = api.getListDetail(id)
                _rvList.value=playlistRes.playlist.tracks
                _coverUrl.value=playlistRes.playlist.coverImgUrl
                _name.value=playlistRes.playlist.name
                _songCounts.value= "${playlistRes.playlist.trackCount}首"
            } catch (e: Exception) {
                Log.d("ljh","歌单初始化出错"+e.message)
            }
        }
    }
}