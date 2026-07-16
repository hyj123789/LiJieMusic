import com.example.net.RetrofitClient
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Test
import retrofit2.http.GET
import retrofit2.http.Url

// 👇 1. 定义一个通用的动态接口
interface DynamicApi {
    // 使用 @Url 注解，告诉 Retrofit 这个请求的路径由参数动态决定
    // 返回 ResponseBody 代表我们只要原始数据，不需要解析成 data class
    @GET
    suspend fun testEndpoint(@Url url: String): ResponseBody
}

// 👇 2. 测试类主体
class ExampleUnitTest {

    @Test
    fun testMusicApi() {
        runBlocking {
            try {
                // 创建通用的动态 API 实例
                val api = RetrofitClient.createApi(DynamicApi::class.java)

                // 🌟🌟🌟 在这里修改你想要测试的 API 后半部分！ 🌟🌟🌟
                // 比如你想测歌单标签，就填 "playlist/highquality/tags"
                // 比如你想测带参数的，就填 "top/playlist/highquality?limit=3"
                val endpoint = "personalized?limit=1"

                println("🚀 正在请求: $endpoint")

                // 发起请求拿到原始响应体
                val responseBody = api.testEndpoint(endpoint)

                // 将响应体转换成纯文本的 JSON 字符串
                val jsonString = responseBody.string()

                println("✅ 成功拿到数据！返回结果如下：")
                println("=====================================================")
                println(jsonString)
                println("=====================================================")

            } catch (e: Exception) {
                println("💥 报错啦: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}