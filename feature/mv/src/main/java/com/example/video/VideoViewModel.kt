package com.example.video

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.base.BaseViewModel
import com.example.net.RetrofitClient
import com.example.video.model.DataTop
import com.example.video.model.DataX
import com.example.video.model.GetTopMvRes
import com.example.video.model.VideoItemWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoViewModel : BaseViewModel() {
    private var allArea: String = "全部"
    private var allType: String = "全部"
    private var topArea: String = "全部"
    private val api = RetrofitClient.createApi(MvApi::class.java)
    private val _allMvList = MutableStateFlow<List<DataX>?>(null)
    private val _topMvList = MutableStateFlow<List<DataTop>?>(null)
    private val _recommendMvRes = MutableStateFlow<List<VideoItemWrapper>>(emptyList())
    private val _toastMsg = MutableStateFlow<String?>(null)
    val allMvList = _allMvList.asStateFlow()
    val topMvList = _topMvList.asStateFlow()
    val recommendMvRes: StateFlow<List<VideoItemWrapper>> = _recommendMvRes
    val toastMsg = _toastMsg.asStateFlow()

    fun fetchAllMv() {
        viewModelScope.launch {
            try {
                val allMvRes = api.getAllMv(allArea, allType)
                if (allMvRes.code != 200) {
                    _toastMsg.value = "网络请求失败"
                    return@launch
                }
                _toastMsg.value = "刷新成功，共${allMvRes.count}条数据"
                _allMvList.value = allMvRes.data
            } catch (e: Exception) {
                Log.d("ljh", "请求AllMV报错啦" + e.message)
            }
        }
    }

    fun fetchTopMv() {
        viewModelScope.launch {
            try {
                if (topArea == "全部") {
                    val topMvRes = api.getTopMv()
                    if (topMvRes.code != 200) {
                        _toastMsg.value = "网络请求失败"
                        Log.d("hhh","请求失败了捏")
                        return@launch
                    }
                    Log.d("hhh","拿到数据了捏")
                    _toastMsg.value = "刷新成功"
                    _topMvList.value = topMvRes.data
                }
                else {
                    val topMvRes = api.getTopMvNormal(area = topArea)
                if (topMvRes.code != 200) {
                    _toastMsg.value = "网络请求失败"
                    Log.d("hhh","请求失败了捏")
                    return@launch
                }
                Log.d("hhh","拿到数据了捏")
                _toastMsg.value = "刷新成功"
                _topMvList.value = topMvRes.data
                }
            } catch (e: Exception){
                Log.e("ljh", "请求TopMV报错啦" + e.message)
            }
        }
    }

    fun updateAllArea(area: String) {
        allArea = area
    }

    fun updateAllType(type: String) {
        allType = type
    }
    fun updateTopArea(area: String){
        topArea = area
    }

    fun fetchRecommendMv(currentOffset: Int) {
        launchRequest {
            try {

                Log.d("hyj", "开启MV的网络请求，currentOffset = ${currentOffset}")
                val response1 = api.getRecommendMv(currentOffset)

                Log.d("hyj", "返回码：${response1.code},Mv 数据量: ${response1.datas?.size}")

                if (response1.code == 200) {
                    _recommendMvRes.value = response1.datas ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("hyj", "网络请求直接崩溃了！罪魁祸首是：${e.message}")
                e.printStackTrace()
            }
        }
    }
}