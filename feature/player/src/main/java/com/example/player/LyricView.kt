package com.example.player

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt
import com.example.player.model.Lyric
import kotlin.math.abs
import kotlin.math.roundToInt

//自动生成多个构造函数
class LyricView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val normalTextSize = 60f
    private val normalTextColor = Color.GRAY
    private val highlightTextColor = "#BDE39F"
    private val lineSpacing = 120f
    private val textHorizontalPadding = 40f
    private val lyricFollowPadding = 80f

    //普通画笔
    private val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = normalTextSize
        color = normalTextColor
    }

    //高亮画笔
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = normalTextSize
        color = highlightTextColor.toColorInt()
    }

    private val karaokeHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = normalTextSize
        color = highlightTextColor.toColorInt()
    }

    private val karaokeNormalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = normalTextSize
        color = normalTextColor
    }

    private var lyrics: List<Lyric> = emptyList()
    private var currentPosition: Long = 0L
    private var currentIndex: Int = 0
    private var wordProgress = 0f
    private var isBrowsing = false
    private var touchDownY = 0f
    private var wasDrag = false
    private var realIndexOnTouchDown = 0
    private val TAP_THRESHOLD = 30f //点击阈值
    private var onSeekListener: ((Long) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private val autoRecoverRunnable = Runnable {
        isBrowsing = false
        invalidate()
    }

    private fun cancelAutoRecover() {
        handler.removeCallbacks(autoRecoverRunnable)
    }

    private fun startAutoRecover() {
        cancelAutoRecover()
        handler.postDelayed(autoRecoverRunnable, 3000)
    }

    fun setLyrics(lyrics: List<Lyric>) {
        this.lyrics = lyrics
        currentIndex = findCurrentIndex()
        isBrowsing = false
        cancelAutoRecover()
        invalidate()
    }

    fun updateProgress(position: Long) {
        if (isBrowsing) return
        this.currentPosition = position
        val oldIndex = currentIndex
        currentIndex = findCurrentIndex()

        //切换歌词重新开始计算
        if (oldIndex != currentIndex) {
            wordProgress = 0f
        }
        invalidate()
    }

    fun setOnSeekListener(listener: (Long) -> Unit) {
        this.onSeekListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (lyrics.isEmpty()) {
            drawEmptyState(canvas)
            return
        }
        //一次性画十一 行
        val start = maxOf(0, currentIndex - 5)
        val end = minOf(lyrics.size - 1, currentIndex + 5)

        val centerX = width / 2f
        val centerY = height / 2f

        for (i in start..end) {
            val lyric = lyrics[i]
            val y = centerY + (i - currentIndex) * lineSpacing
            if (i == currentIndex && lyric.words != null && lyric.words.isNotEmpty()) {
                drawWordByWord(canvas, lyric, centerX, y)
            } else {
                val text = getLyricText(lyric)
                if (isBrowsing) {
                    val paint = normalPaint
                    paint.textSize = normalTextSize
                    drawStaticText(canvas, text, centerX, y, paint)
                    val text = getLyricText(lyrics[currentIndex])
                    val totalWidth = minOf(paint.measureText(text), getAvailableTextWidth())
                    val lineY = height / 2f - normalTextSize * 0.35f
                    val margin = 40f
                    canvas.drawLine(0f, lineY, (width - totalWidth) / 2 - margin, lineY, paint)
                    canvas.drawLine(
                        (width + totalWidth) / 2 + margin,
                        lineY,
                        width.toFloat(),
                        lineY,
                        paint
                    )
                    continue
                }
                val paint = if (i == currentIndex)
                    highlightPaint
                else
                    normalPaint
                paint.textSize = normalTextSize
                drawStaticText(canvas, text, centerX, y, paint)
            }
        }

    }

    private fun drawWordByWord(
        canvas: Canvas,
        lyric: Lyric,
        centerX: Float,
        y: Float
    ) {
        val words = lyric.words ?: return
        if (words.isEmpty()) return
        karaokeNormalPaint.textSize = normalTextSize
        karaokeHighlightPaint.textSize = normalTextSize
        val totalWidth =
            words.sumOf {
                karaokeNormalPaint.measureText(it.content).toDouble()
            }.toFloat()
        val availableWidth = getAvailableTextWidth()
        val hasOverflow = totalWidth > availableWidth
        val textLeft = textHorizontalPadding
        val textRight = width - textHorizontalPadding
        var sungRight = 0f
        var passedWidth = 0f
        if (hasOverflow && !isBrowsing) {
            for (word in words) {
                val wordWidth = karaokeNormalPaint.measureText(word.content)
                if (currentPosition < word.startTime) break
                val progress =
                    if (word.endTime > currentPosition) {
                        (currentPosition - word.startTime).toFloat() / (word.endTime - word.startTime) //算一个字唱了多少
                    } else {
                        1f
                    }.coerceIn(0f, 1f)
                sungRight = passedWidth + wordWidth * progress
                if (progress < 1f) break
                passedWidth += wordWidth
            }
        }
        val followOffset =
            if (hasOverflow && !isBrowsing) {
                (sungRight + lyricFollowPadding - availableWidth)
                    .coerceIn(0f, totalWidth - availableWidth)
            } else {
                0f
            }
        var currentX = if (hasOverflow) {
            textLeft - followOffset
        } else {
            centerX - totalWidth / 2f
        }
        val clipTop = getTextClipTop(y, karaokeHighlightPaint)
        val clipBottom = getTextClipBottom(y, karaokeHighlightPaint)
        val rowSave = if (hasOverflow) {
            canvas.save().also {
                canvas.clipRect(textLeft, clipTop, textRight, clipBottom)
            }
        } else {
            -1
        }
        // 浏览模式保持原样
        if (isBrowsing) {
            for (word in words) {
                canvas.drawText(
                    word.content,
                    currentX,
                    y,
                    karaokeNormalPaint
                )
                currentX += karaokeNormalPaint.measureText(word.content)
            }
            if (rowSave != -1) {
                canvas.restoreToCount(rowSave)
            }
            return
        }
        // 当前播放位置
        val now = currentPosition
        for (word in words) {

            val width = karaokeNormalPaint.measureText(word.content)
            // 先画灰色
            canvas.drawText(word.content, currentX, y, karaokeNormalPaint)
            // 已经开始播放
            if (now >= word.startTime) {
                val nextTime = word.endTime
                val progress =
                    if (nextTime > now) {
                        (now - word.startTime).toFloat() / (nextTime - word.startTime)
                    } else {
                        1f
                    }.coerceIn(0f, 1f)
                val save = canvas.save()
                canvas.clipRect(currentX, clipTop, currentX + width * progress, clipBottom)
                canvas.drawText(word.content, currentX, y, karaokeHighlightPaint)
                canvas.restoreToCount(save)
            }
            currentX += width
        }
        if (rowSave != -1) {
            canvas.restoreToCount(rowSave)
        }
    }

    private fun drawStaticText(
        canvas: Canvas,
        text: String,
        centerX: Float,
        y: Float,
        paint: Paint
    ) {
        val availableWidth = getAvailableTextWidth()
        val textWidth = paint.measureText(text)
        if (textWidth <= availableWidth) {
            val originalAlign = paint.textAlign
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(text, centerX, y, paint)
            paint.textAlign = originalAlign
            return
        }

        val originalAlign = paint.textAlign
        val save = canvas.save()
        paint.textAlign = Paint.Align.LEFT
        //裁剪
        canvas.clipRect(
            textHorizontalPadding,
            getTextClipTop(y, paint),
            width - textHorizontalPadding,
            getTextClipBottom(y, paint)
        )
        canvas.drawText(
            text,
            textHorizontalPadding,
            y,
            paint
        )
        canvas.restoreToCount(save)
        paint.textAlign = originalAlign
    }

    private fun getAvailableTextWidth(): Float {
        return (width - textHorizontalPadding * 2).coerceAtLeast(0f)
    }

    private fun getTextClipTop(y: Float, paint: Paint): Float {
        return y + paint.fontMetrics.ascent - 8f
        //paint.fontMetrics.ascent文字顶部到基准线的距离（负数）
    }

    private fun getTextClipBottom(y: Float, paint: Paint): Float {
        return y + paint.fontMetrics.descent + 8f
        //paint.fontMetrics.descent文字底部到基准线的距离（正数）
    }

    private fun getLyricText(lyric: Lyric): String {
        return if (lyric.words != null && lyric.words.isNotEmpty()) {
            lyric.words.joinToString("") { it.content }
        } else {
            lyric.content ?: ""
        }
    }

    private fun drawEmptyState(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            textSize = 30f
            color = Color.GRAY
        }
        canvas.drawText("暂无歌词资源", width / 2f, height / 2f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownY = event.y
                wasDrag = false
                realIndexOnTouchDown = if (isBrowsing) currentIndex else findCurrentIndex()
                parent?.requestDisallowInterceptTouchEvent(true)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaY = touchDownY - event.y
                if (abs(deltaY) > TAP_THRESHOLD) {
                    wasDrag = true
                    isBrowsing = true
                    val offset = (deltaY / lineSpacing).roundToInt()
                    val newIndex = (realIndexOnTouchDown + offset)
                        .coerceIn(0, maxOf(0, lyrics.size - 1))
                    if (newIndex != currentIndex) {
                        currentIndex = newIndex
                        invalidate()
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                parent?.requestDisallowInterceptTouchEvent(false)

                if (wasDrag) {
                    isBrowsing = true
                    startAutoRecover()
                } else if (isBrowsing) {
                    cancelAutoRecover()
                    val targetTime = lyrics.getOrNull(currentIndex)?.startTime ?: 0L
                    currentPosition = targetTime
                    isBrowsing = false
                    onSeekListener?.invoke(targetTime)
                    invalidate()
                } else {
                    (parent as? View)?.performClick()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                isBrowsing = false
                cancelAutoRecover()
            }
        }
        return true
    }

    private fun findCurrentIndex(): Int {
        if (lyrics.isEmpty()) return 0
        var index = 0
        for (i in lyrics.indices) {
            if (lyrics[i].startTime <= currentPosition) {
                index = i
            }
        }
        return index
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 移除Handler的延迟回调，防止View销毁后Runnable仍然持有LyricView引用导致内存泄漏
        handler.removeCallbacks(autoRecoverRunnable)
    }
}