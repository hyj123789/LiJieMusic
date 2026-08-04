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

//自动生成多个构造函数（重载）
class LyricView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //画笔基础属性的设置
    private val normalTextSize = 60f
    private val normalTextColor = Color.GRAY
    private val highlightTextColor = "#BDE39F"

    //中间歌词空行
    private val lineSpacing = 120f
    private val textHorizontalPadding = 40f
    private val lyricFollowPadding = 80f

    //歌词
    private var lyrics: List<Lyric> = emptyList()
    //播放的位置（时间）
    private var currentPosition: Long = 0L
    //歌词索引
    private var currentIndex: Int = 0
    //逐字播放的时候，单个字的进度
    private var wordProgress = 0f
    //是否处于浏览状态
    private var isBrowsing = false
    //按下的时候Y的坐标
    private var touchDownY = 0f
    //是否在MOVE这个Event，只在单次触摸事件有效
    private var wasDrag = false
    //记录按下的时候歌词的索引
    private var realIndexOnTouchDown = 0
    //阈值，超过这个阈值就被判定位滑动，反之就是一次点击事件
    private val TAP_THRESHOLD = 30f
    //
    private var onSeekListener: ((Long) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private val autoRecoverRunnable = Runnable {
        isBrowsing = false
        invalidate()
    }

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

    //卡拉OK高亮画笔
    private val karaokeHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = normalTextSize
        color = highlightTextColor.toColorInt()
    }

    //卡拉OK普通画笔
    private val karaokeNormalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        textSize = normalTextSize
        color = normalTextColor
    }

    //如果正在滑动，就需要取消三秒后跳转
    private fun cancelAutoRecover() {
        handler.removeCallbacks(autoRecoverRunnable)
    }

    //滑动结束，开启三秒后跳转倒计时
    private fun startAutoRecover() {
        cancelAutoRecover()
        handler.postDelayed(autoRecoverRunnable, 3000)
    }

    //设置数据源，点歌或者切歌的时候会切换
    fun setLyrics(lyrics: List<Lyric>) {
        this.lyrics = lyrics
        currentIndex = findCurrentIndex()
        isBrowsing = false
        cancelAutoRecover()
        invalidate()
    }

    //根据ProgressBar跳转的位置更新歌词
    fun updateProgress(position: Long) {
        //如果正在浏览，不更新
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
    //外部设置的监听者，当歌词滑动更新之后，需要调用这个listener，来更新ProgressBar
    fun setOnSeekListener(listener: (Long) -> Unit) {
        this.onSeekListener = listener
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //歌词是空的？直接返回吧，洗洗睡了
        if (lyrics.isEmpty()) {
            drawEmptyState(canvas)
            return
        }
        //一次性画十一行，当前行和 前五行 后五行
        val start = maxOf(0, currentIndex - 5)
        val end = minOf(lyrics.size - 1, currentIndex + 5)

        val centerX = width / 2f
        val centerY = height / 2f

        for (i in start..end) {
            val lyric = lyrics[i]
            //计算每一行歌词的y位置
            val y = centerY + (i - currentIndex) * lineSpacing
            //如果走到了正在播放的歌词，且有逐字歌词的话，使用逐字歌词画法
            if (i == currentIndex && lyric.words != null && lyric.words.isNotEmpty()) {
                drawWordByWord(canvas, lyric, centerX, y)
            } else {
                //没有逐字歌词或者不是当前行，逐行画
                val text = getLyricText(lyric)
                //如果在浏览的话
                if (isBrowsing) {
                    val paint = normalPaint
                    paint.textSize = normalTextSize
                    drawStaticText(canvas, text, centerX, y, paint)
                    val text = getLyricText(lyrics[currentIndex])
                    val totalWidth = minOf(paint.measureText(text), getAvailableTextWidth())
                    val lineY = height / 2f - normalTextSize * 0.35f //因为基准线是文字的底部
                    val margin = 40f
                    //画一条基准线，表示要不要跳转到这一句歌词
                    canvas.drawLine(0f, lineY, (width - totalWidth) / 2 - margin, lineY, paint)
                    canvas.drawLine(
                        (width + totalWidth) / 2 + margin,
                        lineY,
                        width.toFloat(),
                        lineY,
                        paint
                    )
                    //浏览的话，不需要高亮，直接跳过本次循环即可
                    continue
                }
                //高亮歌词用高亮画笔
                val paint = if (i == currentIndex)
                    highlightPaint
                else
                    normalPaint
                paint.textSize = normalTextSize
                //逐行画静态歌词
                drawStaticText(canvas, text, centerX, y, paint)
            }
        }
    }
    //一个一个字画
    private fun drawWordByWord(
        canvas: Canvas,
        lyric: Lyric,
        centerX: Float,
        y: Float
    ) {
        //空的？直接返回，洗洗睡吧
        val words = lyric.words ?: return
        if (words.isEmpty()) return
        //重新设置，不是重复，避免画笔在某些地方被修改了
        karaokeNormalPaint.textSize = normalTextSize
        karaokeHighlightPaint.textSize = normalTextSize
        val totalWidth =
            words.sumOf {
                karaokeNormalPaint.measureText(it.content).toDouble()
            }.toFloat()
        val availableWidth = getAvailableTextWidth()

        //屏幕装得下吗？？？
        val hasOverflow = totalWidth > availableWidth

        //左右边距
        val textLeft = textHorizontalPadding
        val textRight = width - textHorizontalPadding

        //右边唱到的位置
        var sungRight = 0f
        //之前唱完了多少字的宽度
        var passedWidth = 0f
        //如果溢出屏幕而且不在浏览，跑马灯
        if (hasOverflow && !isBrowsing) {
            //一个个字画
            for (word in words) {
                val wordWidth = karaokeNormalPaint.measureText(word.content)
                //还没到你，可以不管你了
                if (currentPosition < word.startTime) break
                //计算一个字唱的进度
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
        //偏移量
        val followOffset =
            if (hasOverflow && !isBrowsing) {
                (sungRight + lyricFollowPadding - availableWidth)
                    .coerceIn(0f, totalWidth - availableWidth)//最大偏移量就是歌词总宽度-屏幕宽度
            } else {
                0f
            }
        //溢出了直接从可见区域左边followOffset开始画开始画
        var currentX = if (hasOverflow) {
            textLeft - followOffset
        } else {
            centerX - totalWidth / 2f //没溢出，居中显示（画笔属性左对齐）
        }
        //裁剪一下Canvas可画区域，虽然其实外面的其实也不会显示，但会造成性能开销
        val clipTop = getTextClipTop(y, karaokeHighlightPaint)
        val clipBottom = getTextClipBottom(y, karaokeHighlightPaint)
        //存一下没裁剪之前的初始状态
        val rowSave = if (hasOverflow) {
            canvas.save().also {
                canvas.clipRect(textLeft, clipTop, textRight, clipBottom)
            }
        } else {
            -1
        }
        // 浏览模式保持灰色，不高亮
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
            //画完之后，恢复成原来的画布
            if (rowSave != -1) {
                canvas.restoreToCount(rowSave)
            }
            return
        }
        // 当前播放位置
        val now = currentPosition
        words.forEach { word ->

        }
        for (word in words) {

            val width = karaokeNormalPaint.measureText(word.content)
            // 先画灰色
            canvas.drawText(word.content, currentX, y, karaokeNormalPaint)
            // 已经开始播放，计算单个字的播放进度
            if (now >= word.startTime) {
                val nextTime = word.endTime
                val progress =
                    if (nextTime > now) {
                        (now - word.startTime).toFloat() / (nextTime - word.startTime)
                    } else {
                        1f
                    }.coerceIn(0f, 1f)
                //存画布，然后裁剪，然后恢复
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

    //获取屏幕可用宽度，有边距
    private fun getAvailableTextWidth(): Float {
        return (width - textHorizontalPadding * 2).coerceAtLeast(0f)
    }
    //顶部裁剪边界
    private fun getTextClipTop(y: Float, paint: Paint): Float {
        return y + paint.fontMetrics.ascent - 8f
        //paint.fontMetrics.ascent文字顶部到基准线的距离（负数）
    }
    //底部裁剪边界
    private fun getTextClipBottom(y: Float, paint: Paint): Float {
        return y + paint.fontMetrics.descent + 8f
        //paint.fontMetrics.descent文字底部到基准线的距离（正数）
    }
    //获取歌词文本
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

    //处理一下点击事件冲突（切换封面/歌词和跳转到当前歌词）
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownY = event.y
                wasDrag = false
                realIndexOnTouchDown = if (isBrowsing) currentIndex else findCurrentIndex()
                //禁止父布局拦截，因为滑动事件子布局自己处理
                parent?.requestDisallowInterceptTouchEvent(true)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaY = touchDownY - event.y
                //超过阈值，判定位滑动，浏览状态
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
                //一次事件结束，恢复父布局拦截能力
                parent?.requestDisallowInterceptTouchEvent(false)

                if (wasDrag) {
                    isBrowsing = true
                    startAutoRecover()
                } else if (isBrowsing) {
                    //启动三秒计时咯
                    cancelAutoRecover()
                    val targetTime = lyrics.getOrNull(currentIndex)?.startTime ?: 0L
                    currentPosition = targetTime
                    isBrowsing = false
                    onSeekListener?.invoke(targetTime)
                    invalidate()
                } else {
                    //触发父布局的点击事件
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

    //找到当前歌词的索引
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