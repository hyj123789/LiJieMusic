package com.example.mv

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.base.BaseViewModel
import com.example.net.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MVViewModel : BaseViewModel(){
    private val _MVFlow = MutableStateFlow<List<VideoItemWrapper>>(emptyList())

    val MVFlow : StateFlow<List<VideoItemWrapper>> get() = _MVFlow

    fun fetchRecommendPlaylists(currentOffset : Int) {
        launchRequest {
            try {
                //创建 Api 实例
                val api = RetrofitClient.createApi(MVApi::class.java)

                //发起请求拿数据
                Log.d("hyj", "开启MV的网络请求，currentOffset = ${currentOffset}")
                val response1 = api.getmv(currentOffset)

                Log.d("hyj", "返回码：${response1.code},Mv 数据量: ${response1.datas?.size}")

                //如果成功拿到数据就放进
                if (response1.code == 200) {
                    _MVFlow.value = response1.datas ?: emptyList()
                }
            } catch (e: Exception) {
                //核心排查代码：强行打印真实的报错原因
                Log.e("hyj", "网络请求直接崩溃了！罪魁祸首是：${e.message}")
                e.printStackTrace() // 这行会在控制台打印红色的完整错误堆栈
            }
        }
    }
}