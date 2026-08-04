package com.example.video

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.base.BaseViewModel
import com.example.net.RetrofitClient
import com.example.video.model.DataTop
import com.example.video.model.DataX
import com.example.video.model.GetMvDetailRes
import com.example.video.model.HotComment
import com.example.video.model.VideoItemWrapper
import com.example.video.resource.MyPagingSource
import com.example.video.resource.TopPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class VideoViewModel : BaseViewModel() {
    private val _topArea = MutableStateFlow("港台")
    private val api = RetrofitClient.createApi(MvApi::class.java)

    private val _allArea = MutableStateFlow("全部")
    private val _allType = MutableStateFlow("全部")

    private val _recommendMvRes = MutableStateFlow<List<VideoItemWrapper>>(emptyList())
    private val _toastMsg = MutableStateFlow<String?>(null)
    private val _mvDetail = MutableStateFlow<GetMvDetailRes?>(null)
    private val _mvUrl = MutableStateFlow<String?>(null)

    private val _mvComment = MutableStateFlow<List<HotComment>?>(null)
    val recommendMvRes = _recommendMvRes.asStateFlow()
    val toastMsg = _toastMsg.asStateFlow()
    val mvDetail = _mvDetail.asStateFlow()
    val mvUrl = _mvUrl.asStateFlow()

    val mvComment = _mvComment
    val allMvPagingFlow: Flow<PagingData<DataX>> =
        combine(_allArea, _allType) { area, type ->
            area to type
        }.flatMapLatest { (area, type) ->//核心概念：当上游数据发生变化时，取消之前正在执行的协程，转而执行新的协程。
            Pager(PagingConfig(pageSize = 30, initialLoadSize = 30)) {
                MyPagingSource(api, area, type)
            }.flow
        }.cachedIn(viewModelScope)

    val topMvPagingFlow: Flow<PagingData<DataTop>> =
        _topArea.flatMapLatest { area ->
            Pager(PagingConfig(pageSize = 30)) {
                TopPagingSource(api, area)
            }.flow
        }.cachedIn(viewModelScope)

    fun updateAllArea(area: String) {
        _allArea.value = area
    }

    fun updateAllType(type: String) {
        _allType.value = type
    }

    fun fetchMvUrl(id: Long) {
        if (id == 0L) return
        viewModelScope.launch {
            try {
                val urlRes = api.getMvUrl(id)
                if (urlRes.code != 200) {
                    Log.d("ljh", "加载MV资源失败")
                    _toastMsg.value = "网络请求失败"
                    return@launch
                }
                if (urlRes.data.code != 200) {
                    _toastMsg.value = "网络请求失败${urlRes.data.msg}"
                } else _mvUrl.value = urlRes.data.url
            } catch (e: Exception) {
                Log.e("ljh", "MVURL网络请求失败" + e.message)
                _toastMsg.value = "网络请求失败"
            }
        }
    }

    fun fetchMvDetail(id: Long) {
        if (id == 0L) return
        viewModelScope.launch {
            try {
                val mvDetail = api.getMvDetail(id)
                if (mvDetail.code != 200) {
                    _toastMsg.value = "获取MV详情失败"
                } else _mvDetail.value = mvDetail
            } catch (e: Exception) {
                Log.e("ljh", "MV详情网络请求异常" + e.message)
                _toastMsg.value = "网络错误"
            }
        }
    }

    fun updateTopArea(area: String) {
        _topArea.value = area
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

    fun fetchMvComment(id: Long) {
        launchRequest {
            try {

                val response1 = api.getMvComment(id)
                Log.d("hyj", "mv返回的数目大小：${response1.total}")

                if (response1.total != 0) {
                    _mvComment.value = response1.hotComments ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("hyj", "mv评论网络请求直接崩溃了！罪魁祸首是：${e.message}")
                e.printStackTrace()
            }
        }
    }
}

