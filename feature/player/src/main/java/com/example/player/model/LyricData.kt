package com.example.player.model

import android.util.Log
import com.google.gson.annotations.SerializedName


data class LyricResponse(
    val code : Int,
    @SerializedName("lrc")
    val lineLyric : OriginLyric,
    @SerializedName("yrc")
    val wordLyric : OriginLyric
)
data class OriginLyric(
    val version : Int,
    val lyric: String
)
data class SentenceLyric(
    val time: Long,      // 开始时间（毫秒）
    val content: String  // 歌词内容
)


data class WordLyric(
    val startTime: Long,     // 单词开始时间
    val duration: Int,       // 单词持续时长
    val content: String      // 单个字
)

data class Lyric(
    val startTime: Long,     // 行开始时间
    val totalDuration: Int,  // 行总时长
    val words: List<WordLyric>?,  // 如果有逐字歌词，这里不为空
    val content: String? = null,
)

object LyricParser {

    /**
     * 解析逐句歌词
     * 输入："[00:12.570]难以忘记初次见你\n[00:16.860]一双迷人的眼睛"
     * 输出：List<SentenceLyric>
     */
    fun parseSentenceLyric(raw: String): List<SentenceLyric> {
        val result = mutableListOf<SentenceLyric>()

        // 正则：匹配 [分钟:秒.毫秒]歌词内容
        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})\](.*)""")

        raw.split("\n").forEach { line ->
            regex.find(line)?.let { match ->
                val minute = match.groupValues[1].toLong()
                val second = match.groupValues[2].toInt()
                val millis = match.groupValues[3].padEnd(3, '0').toInt()
                val content = match.groupValues[4].trim()

                if (content.isNotEmpty()) {
                    val totalMillis = (minute * 60 + second) * 1000 + millis
                    result.add(SentenceLyric(totalMillis, content))
                }
            }
        }

        return result.sortedBy { it.time }
    }

    fun parseWordLyric(raw: String): List<Lyric> {
        val result = mutableListOf<Lyric>()

        // 行正则：匹配 [start,duration]
        val lineRegex = Regex("""\[(\d+),(\d+)\]""")

        // 字正则：匹配 (start,duration,0)字
        val wordRegex = Regex("""\((\d+),(\d+),\d+\)([^()]+)""")

        raw.split("\n").forEach { line ->
            // 先找行的时间标记 [start,duration]
            val lineMatch = lineRegex.find(line)
            if (lineMatch != null) {
                val startTime = lineMatch.groupValues[1].toLong()
                val totalDuration = lineMatch.groupValues[2].toInt()

                // 再找这一行里的所有字
                val words = mutableListOf<WordLyric>()
                wordRegex.findAll(line).forEach { wordMatch ->
                    val wordStart = wordMatch.groupValues[1].toLong()
                    val wordDuration = wordMatch.groupValues[2].toInt()
                    val content = wordMatch.groupValues[3]

                    words.add(WordLyric(wordStart, wordDuration, content))
                }

                // 如果有字，创建歌词行；如果没有字（空行），跳过
                if (words.isNotEmpty()) {
                    result.add(Lyric(startTime, totalDuration, words))
                }
            }
        }

        return result.sortedBy { it.startTime }
    }

    fun parseLyric(lrcLyric: String, yrcLyric: String): List<Lyric> {
        if (yrcLyric.isNullOrEmpty()) Log.d("test_lyric","我朝为，逐字歌词是空的？？？！！！")
        else Log.d("test_lyric","乱说，哪里是空的")
        // 1. 优先使用逐字歌词（yrc）
        if (yrcLyric.isNotEmpty()) {
            val wordLyrics = parseWordLyric(yrcLyric)
            if (wordLyrics.isNotEmpty()) {
                return wordLyrics
            }
        }

        // 2. 逐字没有，使用逐句歌词（lrc）
        if (lrcLyric.isNotEmpty()) {
            val sentenceLyrics = parseSentenceLyric(lrcLyric)
            // 将 SentenceLyric 转换为 Lyric
            return sentenceLyrics.map { sentence ->
                Lyric(
                    startTime = sentence.time,
                    totalDuration = 0,  // 逐句没有时长信息
                    words = null,        // null 表示逐句模式
                    content = sentence.content,  // ← 新增：传入歌词文本
                )
            }
        }

        // 3. 都没有，返回空列表
        return emptyList()
    }
}