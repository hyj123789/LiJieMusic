//最外层响应
data class SongHistoryWikiResponse(
    val code: Int,
    val data: SongWikiData?,
    val message: String?
)

//包含三个核心模块的数据
data class SongWikiData(
    val songInfoDto: SongInfoDto?,
    val musicFirstListenDto: MusicFirstListenDto?,
    val musicTotalPlayDto: MusicTotalPlayDto?
)

//用于顶部标题栏展示歌曲名
data class SongInfoDto(
    val songName: String
)

//第一次听
data class MusicFirstListenDto(
    val date: String, //用于展示日期
    val season : String,//季节
    val period : String ,//时段
)

//累计播放
data class MusicTotalPlayDto(
    val playCount: Int, //用于展示次数
    val text: String    //用于展示下方文案
)