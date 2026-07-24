package com.example.player.model;

data class SongApiResponse(
    val code: Int,
    val message: String,
    val data: SongData
)

data class SongData(
    val songId: Long,
    val songName: String,
    val songSubTitle: String?,
    val company: String?,
    val publishTime: Long?,
    val language: String?,
    val originalCover: Int,
    val no: String?,
    val disc: String?,
    val transName: String?,
    val mvIds: List<Int>?,
    val lyricContent: String?,
    val transLyricContent: String?,
    val playUrl: String?,
    val forTransLyric: Int,
    val noNeedLyric: Int,
    val lyricLock: Int,
    val transLyricLock: Int,
    val lyricIsEdited: Int,
    val duration: Long,
    val artistRepVos: List<Artist>?,
    val lyricArtists: List<Artist>?,
    val composeArtists: List<Artist>?,
    val arrangeArtists: List<Artist>?,
    val roleArtists: List<Artist>?,
    val songTags: List<String>?,
    val albumRepVo: Album?
)

data class Artist(
    val artistId: Long,
    val artistName: String,
    val alias: List<String>?,
    val headPicUrl: String?,
    val area: String?,
    val type: Int?,
    val desc: String?,
    val production: String?,
    val avatarPicUrl: String?,
    val transName: String?
)

data class Album(
    val albumId: Long,
    val albumName: String,
    val artistRepVos: List<Artist>?,
    val albumPicUrl: String?,
    val albumSubTitle: String?,
    val company: String?,
    val publishTime: Long?,
    val songRepVos: List<SongData>?,
    val songTags: List<String>?,
    val production: String?,
    val language: String?,
    val type: Int?,
    val transName: String?
)