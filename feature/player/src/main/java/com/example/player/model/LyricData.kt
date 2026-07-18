package com.example.player.model

import java.util.regex.Pattern


data class LyricResponse(
    val lrc: LrcData?
)

data class LrcData(
    val lyric: String?
)
data class LyricLine(
    val timeMillis: Long,
    val text: String
)

object LyricUtil {
    // 2. 解析算法：把 "[00:16.797] 北风毫不留情" 变成实体类
    fun parseLyric(lrcString: String?): List<LyricLine> {
        if (lrcString.isNullOrEmpty()) return emptyList()

        val lyricLines = mutableListOf<LyricLine>()
        // 按回车键 \n 把超长的字符串切成一行行的数组
        val lines = lrcString.split("\n")

        // 用正则表达式匹配 [分钟:秒.毫秒] 的格式
        val pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\]")

        for (line in lines) {
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                // 把分、秒、毫秒提取出来
                val min = matcher.group(1)?.toLong() ?: 0L
                val sec = matcher.group(2)?.toLong() ?: 0L
                val msStr = matcher.group(3) ?: "0"

                // 处理毫秒可能是两位数(10ms)或三位数(1ms)的情况
                val ms = if (msStr.length == 2) msStr.toLong() * 10 else msStr.toLong()

                // 算出这一句歌词的绝对毫秒数
                val time = min * 60 * 1000 + sec * 1000 + ms

                // 把中括号后面的文字截取出来，去掉首尾空格
                val text = line.substring(matcher.end()).trim()

                // 如果这行确实有歌词（不是纯音乐过渡），就加进列表
                if (text.isNotEmpty()) {
                    lyricLines.add(LyricLine(time, text))
                }
            }
        }
        return lyricLines
    }
}