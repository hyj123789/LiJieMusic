package com.example.therouter
/**
 * 应用页面路由表。
 *
 * 命名规则：
 * 1. 常量名：模块名_页面名
 * 2. 路由值：/模块名/页面名
 * 3. 路径全部使用小写
 * 4. 页面参数不拼接到路由中
 */
object RoutePath {
    const val MAIN_ACTIVITY = "/app/main"
    const val LOGIN_MAIN = "/login/main"
    const val HOME_MAIN = "/home/main"
    const val SEARCH_MAIN = "/search/main"
    const val PLAYER_MAIN = "/player/main"
    const val PLAYLIST_MAIN = "/playlist/main"
    const val DYNAMICS_MAIN = "/dynamics/main"
    const val LAUNCH_MAIN = "/app/launch"
}
object RouteParams {

    /** 播放器模块参数 */
    object PlayerParams {
        const val SONG_NAME = "songName"
        const val DURATION = "duration"
        const val ALBUM_ID = "albumId"
    }

    /** 搜索模块参数 */
    object SearchParams {
        const val KEYWORD = "keyword"
        const val PAGE_INDEX = "pageIndex"
    }

    /** 用户中心模块参数 */
    object UserParams {
        const val USER_ID = "userId"
        const val IS_VIP = "isVip"
    }
    /** 歌单模块参数 */
    object PlaylistParams{
        const val PLAYLIST_ID = "playlistId"
    }
}