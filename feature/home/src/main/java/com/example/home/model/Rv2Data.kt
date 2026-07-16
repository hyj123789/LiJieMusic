package com.example.home.model

data class Rv2Data(
    val code: Int,
    val result: List<PlaylistInfo2>
)

data class PlaylistInfo2(
    val id: Long,          //歌单ID
    val name: String,      //歌单名字
    val picUrl: String,    //封面图链接
    val playCount: Long,   //播放量
    val trackCount: Int    //歌曲数量
)
//{
//    "hasTaste": false,
//    "code": 200,
//    "category": 0,
//    "result": [
//    {
//        "id": 7617373821,
//        "type": 0,
//        "name": "2025网络那些超好听的流行热歌(更新快)",
//        "copywriter": "",
//        "picUrl": "https://p4.music.126.net/LajQf5l2QF0AtJeLWOFVlg==/109951168002846018.jpg",
//        "canDislike": true,
//        "trackNumberUpdateTime": 1666772978619,
//        "playCount": 323701,
//        "trackCount": 137,
//        "highQuality": false,
//        "alg": "byplaylist_play_ol_swing"
//    }
//    ]
//}