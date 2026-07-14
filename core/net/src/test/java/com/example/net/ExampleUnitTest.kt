package com.example.net

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import retrofit2.http.GET
import retrofit2.http.Query

// 👇 1. 直接把临时数据类写在测试文件里
data class TempSongInfo(val id: Long, val url: String?, val br: Int)

// 👇 2. 直接把临时接口写在测试文件里
interface TempApi {
    @GET("song/url")
    suspend fun getSongUrl(@Query("id") musicId: String): ApiResponse<List<TempSongInfo>>
}

// 👇 3. 测试类主体
class ExampleUnitTest {
    @Test
    fun testMusicApi() {
        runBlocking {
            try {
                // 直接用上面定义的临时接口
                val api = RetrofitClient.createApi(TempApi::class.java)
                val response = api.getSongUrl("1969519579")

                if (response.code == 200) {
                    val songInfo = response.data?.firstOrNull()
                    println("🎉 成功拿到网易云音乐 URL: ${songInfo?.url}")
                }
            } catch (e: Exception) {
                println("💥 报错啦: ${e.message}")
            }
        }
    }
}