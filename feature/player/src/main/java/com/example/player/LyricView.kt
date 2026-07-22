package com.example.player

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.player.model.Lyric
import kotlin.math.abs
import kotlin.math.roundToInt

class LyricView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val normalTextSize = 60f
    private val highlightTextSize = 70f
    private val normalTextColor = Color.GRAY
    private val highlightTextColor = Color.GREEN
    private val lineSpacing = 120f

    private val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = normalTextSize
        color = normalTextColor
    }

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = highlightTextSize
        color = highlightTextColor
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private val karaokeHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = normalTextSize
        color = highlightTextColor
    }

    private val karaokeNormalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = normalTextSize
        color = normalTextColor
    }

    private var lyrics: List<Lyric> = emptyList()
    private var currentPosition: Long = 0L
    private var currentIndex: Int = 0


    private var isBrowsing = false
    private var touchDownY = 0f
    private var wasDrag = false
    private var realIndexOnTouchDown = 0
    private val TAP_THRESHOLD = 30f

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
        handler.postDelayed(autoRecoverRunnable, 5000)
    }

    fun setLyrics(lyrics: List<Lyric>) {
        this.lyrics = lyrics
        currentIndex = findCurrentIndex()
        isBrowsing = false  // 切歌时重置浏览状态
        cancelAutoRecover()
        invalidate()
    }

    fun updateProgress(position: Long) {
        this.currentPosition=position
        if (isBrowsing) return
        this.currentPosition = position
        currentIndex = findCurrentIndex()
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
                if(isBrowsing){
                    val paint =normalPaint
                    canvas.drawText(text, centerX, y, paint)
                    val text = getLyricText(lyrics[currentIndex])
                    val totalWidth = paint.measureText(text)
                    val lineY = height / 2f - normalTextSize * 0.35f
                    val margin = 40f
                    canvas.drawLine(0f, lineY, (width - totalWidth) / 2 - margin, lineY, paint)
                    canvas.drawLine((width + totalWidth) / 2 + margin, lineY, width.toFloat(), lineY, paint)
                    continue
                }
                val paint = if (i == currentIndex) highlightPaint else normalPaint
                canvas.drawText(text, centerX, y, paint)
            }
        }
    }

    private fun drawWordByWord(canvas: Canvas, lyric: Lyric, centerX: Float, y: Float) {
        val words = lyric.words ?: return
        if (words.isEmpty()) return

        val totalWidth = words.sumOf { karaokeNormalPaint.measureText(it.content).toDouble() }.toFloat()
        var currentX = centerX - totalWidth / 2f
        if(isBrowsing){
            for (word in words) {
                val paint =karaokeNormalPaint
                canvas.drawText(word.content, currentX, y, paint)
                currentX += paint.measureText(word.content)
                val lineY = height / 2f - normalTextSize * 0.35f
                val margin = 40f
                canvas.drawLine(0f, lineY, (width - totalWidth) / 2 - margin, lineY, paint)
                canvas.drawLine((width + totalWidth) / 2 + margin, lineY, width.toFloat(), lineY, paint)
            }
            return
        }
        for (word in words) {
            val isHighlighted = word.startTime <= currentPosition
            val paint = if (isHighlighted) karaokeHighlightPaint else karaokeNormalPaint
            canvas.drawText(word.content, currentX, y, paint)
            currentX += paint.measureText(word.content)
        }
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
}

