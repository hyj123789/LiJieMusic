package com.example.search.model

data class GuessData(
    val code: Int,
    val result: HotSearchResult?
)

data class HotSearchResult(
    val hots: List<HotSearchItem>?
)

data class HotSearchItem(
    val first: String,
    val second: Int,
    val iconType: Int
)


//"code": 200,
//"result": {
//    "hots": [
//    {
//        "first": "周旋",
//        "second": 1,
//        "third": null,
//        "iconType": 1
//    },
//    {
//        "first": "海屿你",
//        "second": 1,
//        "third": null,
//        "iconType": 1
//    },
//    {
//        "first": "赵露思新歌小小",
//        "second": 1,
//        "third": null,
//        "iconType": 1
//    },
//    {
//        "first": "讨厌",
//        "second": 1,
//        "third": null,
//        "iconType": 1
//    },