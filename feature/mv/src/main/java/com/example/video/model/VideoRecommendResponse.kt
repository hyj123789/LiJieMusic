package com.example.video.model

data class VideoRecommendResponse(
    val code : Int,
    val datas: List<VideoItemWrapper>? //对应 JSON 里的 "datas" 数组
)

data class VideoItemWrapper(
    val data: VideoData? // 对应 JSON 里的 "data" 对象
)

data class VideoData(
    val title: String?,         //视频标题
    val description: String?,   //视频描述
    val coverUrl: String?,      //视频封面
    val creator: Creator?,      //创作者信息
    val urlInfo: UrlInfo?       //视频链接信息
)

data class Creator(
    val nickname: String?       //作者名字
)

data class UrlInfo(
    val url: String?            //MP4播放地址
)


//"datas": [
//{
//    "type": 1,
//    "displayed": false,
//    "alg": "special_first_page_rcmd",
//    "extAlg": null,
//    "data": {
//    "alg": "special_first_page_rcmd",
//    "scm": "1.music-video-timeline.video_timeline.video.181017.-295043608",
//    "threadId": "R_VI_62_73750386141D0185968A9D43EBA1EB2F",
//    "coverUrl": "https://p3.music.126.net/M_GYu91yaWcGGSjNE6ipZQ==/109951164867798369.jpg",
//    "height": 1080,
//    "width": 1920,
//    "title": "【致敬K歌之王】这些年TFBOYS弟弟们翻唱过的陈奕迅金曲！",
//    "description": "三个弟弟眼睛里有星星，声音里有彩虹，用满满少年气翻唱陈奕迅的经典金曲！",
//    "commentCount": 41,
//    "shareCount": 47,
//    "resolutions": [
//    {
//        "resolution": 240,
//        "size": 50491939
//    },
//    {
//        "resolution": 480,
//        "size": 80161674
//    },
//    {
//        "resolution": 720,
//        "size": 114244264
//    },
//    {
//        "resolution": 1080,
//        "size": 191954467
//    }
//    ],
//    "creator": {
//    "defaultAvatar": false,
//    "province": 310000,
//    "authStatus": 0,
//    "followed": false,
//    "avatarUrl": "http://p3.music.126.net/rlkSvdxjdVxSIBjTERBv1w==/109951164495967113.jpg",
//    "accountStatus": 0,
//    "gender": 1,
//    "city": 310101,
//    "birthday": 658252800000,
//    "userId": 383936966,
//    "userType": 204,
//    "nickname": "歪果wiggle电台",
//    "signature": "高品质网络电台，优质音乐故事节目。",
//    "description": "",
//    "detailDescription": "",
//    "avatarImgId": 109951164495967120,
//    "backgroundImgId": 109951164006854540,
//    "backgroundUrl": "http://p3.music.126.net/uc0hrJBVb4I9hGT11XFp6w==/109951164006854541.jpg",
//    "authority": 0,
//    "mutual": false,
//    "expertTags": null,
//    "experts": {
//    "1": "音乐原创视频达人"
//},
//    "djStatus": 10,
//    "vipType": 0,
//    "remarkName": null,
//    "avatarImgIdStr": "109951164495967113",
//    "backgroundImgIdStr": "109951164006854541"
//},
//    "urlInfo": {
//    "id": "73750386141D0185968A9D43EBA1EB2F",
//    "url": "http://vodkgeyttp9.vod.126.net/vodkgeyttp8/4gj4w2vo_2959089821_hd.mp4?ts=1784289570&rid=17B41A8EC1FA53DFE49F1C68478E6E37&rl=3&rs=EYHFSQdhNqGlYfHUahoOkgzPPwqAXABu&sign=248ef6cea6789cd6b65079c7c96e4607&ext=MwaY670%2FK1Fgz%2F5oXopanid6vc6BHzADVFqyA9txCHZiqY6qGr%2Bh%2F48y2RTCY%2BRsP3Ab5A%2FlW7h7WULIph7acaQWjwfJdJJfcL%2Fo0SPsTPEbxEcPACbIOXtviCYVNFuo1yRrd6Ihkg8QTLnQP481NnUgHh4%2FwNodyuymy2fNBb9XpcGOoQ02YVlSC1b5zOWNIosgc9Go2ESrnXF282nN3bWrIBlWcmIyiZ%2FG20LPpVtdEoFeY6%2B37DmWCSaXW%2BynaaWj%2FUoWc%2BIQpqI13h1TALKrmlJIp8sNaftFypN88JHyhE7vXgSze0H%2BkeARg7f3Ao%2Fl5EkCnZVdA54X1qBpJg%3D%3D",
//    "size": 80161674,
//    "validityTime": 1200,
//    "needPay": false,
//    "payInfo": null,
//    "r": 480