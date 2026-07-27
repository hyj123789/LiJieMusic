package com.example.playlist

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.net.RetrofitClient
import com.example.playlist.model.Track
import com.example.util.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Url
import java.io.ByteArrayOutputStream

class PlaylistViewModel : ViewModel() {
    private val api = RetrofitClient.createApi(PlayListApi::class.java)
    private val _song = MutableStateFlow<List<Track>?>(null)
    private val _rvList = MutableStateFlow<List<Track>?>(null)
    private val _coverUrl = MutableStateFlow<String?>(null)
    private val _name = MutableStateFlow<String?>(null)
    private val _songCounts = MutableStateFlow<String?>(null)
    private val _toastMsg = MutableStateFlow<String?>(null)

    val song: StateFlow<List<Track>?> = _song
    val rvList: StateFlow<List<Track>?> = _rvList
    val coverUrl: StateFlow<String?> = _coverUrl
    val name: StateFlow<String?> = _name
    val songCounts: StateFlow<String?> = _songCounts
    val toastMsg = _toastMsg.asStateFlow()
    fun init(id: String) {
        viewModelScope.launch {
            try {
                val playlistRes = api.getListDetail(id)
                _rvList.value = playlistRes.playlist.tracks
                _coverUrl.value = playlistRes.playlist.coverImgUrl
                _name.value = playlistRes.playlist.name
                _song.value = playlistRes.playlist.tracks
                _songCounts.value = "${playlistRes.playlist.trackCount}首"
                Log.d("ljh", "歌曲信息" + playlistRes.playlist.toString())
            } catch (e: Exception) {
                Log.d("ljh", "歌单初始化出错" + e.message)
            }
        }
    }

    fun removeSong(pid: Long, ids: String) {
        viewModelScope.launch {
            try {
                val deleteSongFromList = api.deleteSongFromList(pid = pid, ids = ids)
                if (deleteSongFromList.code == 200) {
                    Log.d("ljh", "成功从歌单中删除歌曲")
                    _toastMsg.value = "删除成功"
                } else {
                    _toastMsg.value = "删除失败"
                }
            } catch (e: Exception) {
                Log.e("ljh", "从歌单中删除歌曲出错" + e.message)
                _toastMsg.value = "删除失败"
            }
        }
    }

    fun toggleSong(pid: Long, ids: String) {
        viewModelScope.launch {
            try {
                val toggleSongOrder = api.toggleSongOrder(pid, ids)
                if (toggleSongOrder.code == 200) {
                    _toastMsg.value = "调整顺序成功"
                } else {
                    _toastMsg.value = "调整顺序失败"
                }
            } catch (e: Exception) {
                Log.e("ljh", "调整顺序失败" + e.message)
                _toastMsg.value = "调整顺序失败"
            }
        }
    }

    fun uploadCover(pid: Long, uri: Uri, contentResolver: ContentResolver) {
        try {
            viewModelScope.launch{ // 1. 从 Uri 读取图片
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)  // 85%质量
                val bytes = stream.toByteArray()
                bitmap.recycle()
                val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
                val part = MultipartBody.Part.createFormData("imgFile", "cover.jpg", requestBody)
                val updateCover = api.updateCover(pid, part)
                if (updateCover.code==200){
                    _toastMsg.value="上传成功"
                }else _toastMsg.value="上传失败"
            }
        } catch (e: Exception) {
            Log.d("ljh","上传歌单封面失败谢谢")
            _toastMsg.value="上传失败"
        }
    }
}