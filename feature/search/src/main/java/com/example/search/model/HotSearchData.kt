package com.example.search.model

data class HotSearchResponse(
    val code: Int,
    val data: List<HotSearchData>?
)

data class HotSearchData(
    val searchWord: String, //搜索词
    val score: Int,         //热度分
    val content: String?,   //描述
    val iconUrl: String?    //右侧的小图标
)

//    "code": 200,
//    "trp": {
//    "rules": [
//    "HOT_SEARCH_SONG#@#::亲爱的::attribute$isOperation$4",
//    "HOT_SEARCH_SONG#@#::荧光海（Feat.Top.Barry)::attribute$isOperation$3"
//    ]
//},
//    "data": [
//    {
//        "score": 84904,
//        "iconType": 4,
//        "searchWord": "周旋",
//        "source": 0,
//        "iconUrl": "https://p1.music.126.net/IBKnY_RCYTUAALcqWhAT6g==/109951163967994693.png",
//        "content": "",
//        "url": ""
//    },
//    {
//        "score": 62752,
//        "iconType": 0,
//        "searchWord": "讨厌",
//        "source": 0,
//        "content": "",
//        "url": ""
//    },
//    {