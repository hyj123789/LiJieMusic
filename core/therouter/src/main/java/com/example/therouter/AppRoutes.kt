package com.example.lijiemusic.core.navigation

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
    const val LOGIN_MAIN = "/login/main"
    const val HOME_MAIN = "/home/main"
    const val SEARCH_MAIN = "/search/main"
    const val PLAYLIST_DETAIL = "/playlist/detail"
    const val PLAYER_MAIN = "/player/main"
}
