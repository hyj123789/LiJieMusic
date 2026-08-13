package com.example.profile.network.bean

import kotlinx.serialization.SerialName

/**
 * @author Ben
 * @date 2026/8/13 15:37
 * @description 请求歌单时拿到的元数据，但是没有歌曲
 * */
data class Playlist(
    val adType: Int, // 广告类型（0表示无广告）
    val anonimous: Boolean, // 是否匿名歌单
    val artists: Any, // 艺术家信息（具体类型需根据API确定）
    val backgroundCoverId: Int, // 背景封面ID
    val backgroundCoverUrl: Any, // 背景封面URL（可能为null或字符串）
    val cloudTrackCount: Int, // 云盘歌曲数量
    val commentThreadId: String, // 评论线程ID（用于获取评论）
    val containsTracks: Boolean, // 是否包含歌曲
    val copied: Boolean, // 是否为复制歌单
    val coverImgId: Long, // 封面图片ID
    @SerialName("coverImgId_str")
    val coverImgIdStr: String, // 封面图片ID的字符串形式
    val coverImgUrl: String, // 封面图片URL
    val createTime: Long, // 创建时间（时间戳）
    val creator: Creator, // 创建者信息（需定义Creator类）
    val description: String, // 歌单描述
    val englishTitle: Any, // 英文标题（可能为null）
    val highQuality: Boolean, // 是否为高品质歌单
    val id: Long, // 歌单唯一ID
    val mix: Boolean, // 是否为混合歌单
    val name: String, // 歌单名称
    val newImported: Boolean, // 是否为新导入歌单
    val opRecommend: Boolean, // 是否运营推荐
    val ordered: Boolean, // 是否有序
    val playCount: Int, // 播放次数
    val privacy: Int, // 隐私设置（0公开/1私密/2部分可见）
    val recommendInfo: Any, // 推荐信息（具体结构需根据API）
    val shareStatus: Any, // 分享状态（可能为null或对象）
    val sharedUsers: Any, // 分享用户列表
    val specialType: Int, // 特殊类型（0普通/1视频歌单等）
    val status: Int, // 状态码（0正常/1删除等）
    val subscribed: Any, // 订阅状态（可能为null或布尔值）
    val subscribedCount: Int, // 订阅（收藏）数量
    val subscribers: List<Any>, // 订阅者列表
    val tags: List<Any>, // 标签列表
    val titleImage: Int, // 标题图片资源ID
    val titleImageUrl: Any, // 标题图片URL（可能为null）
    val top: Boolean, // 是否置顶
    val totalDuration: Int, // 总时长（秒）
    val trackCount: Int, // 歌曲总数
    val trackNumberUpdateTime: Long, // 歌曲数量更新时间戳
    val trackUpdateTime: Long, // 歌曲列表更新时间戳
    val tracks: Any, // 歌曲列表（具体类型需确定）
    val updateFrequency: Any, // 更新频率（可能为null或字符串）
    val updateTime: Long, // 歌单信息更新时间戳
    val userId: Long // 创建者用户ID
)