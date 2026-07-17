package com.example.searchpage

data class PlaylistResponse(
    val code: Int,
    val playlists: List<PlaylistItem>? // 🌟 这里的名字必须和 JSON 里的 "playlists" 一模一样
)
data class PlaylistItem(
    val id: Long,
    val name: String,         //对应副标题
    val coverImgUrl: String,  //对应背景图
    val tags: List<String>?   //取第一个tag作为主标题
)

//"playlists": [
//{
//    "name": "可爱摇滚｜一剂抵挡春困的上好良药",
//    "id": 6666112560,
//    "trackNumberUpdateTime": 1773660381543,
//    "status": 0,
//    "userId": 341030416,
//    "createTime": 1616028342548,
//    "updateTime": 1773717413000,
//    "subscribedCount": 36065,
//    "trackCount": 125,
//    "cloudTrackCount": 0,
//    "coverImgUrl": "http://p4.music.126.net/SA6bW1UlPP04rFB2pj-FvQ==/109951165813403264.jpg",
//    "coverImgId": 109951165813403260,
//    "description": "可爱的摇滚是怎么样的呢？\n编曲“妙思清奇”，听感极具丰富，上头洗脑不按常理出牌，旋律另类新趣令人诧异，颠覆广大听众对摇滚的刻板印象，原来摇滚还能这么可爱。\n\n拯救春困计划——提神醒脑，妙趣横生。听一首可爱的歌儿，予你一剂抵挡春困的上好良药， 给正在工作／学习犯困疲劳的你充充值，加加血吧。\n\n关键词：可爱、欢快、甜系、鬼畜（谨慎食用）\n\n封面：英国著名儿歌合唱团Blur（生活是垃圾）",
//    "tags": [
//    "欧美",
//    "摇滚",
//    "快乐"
//    ],