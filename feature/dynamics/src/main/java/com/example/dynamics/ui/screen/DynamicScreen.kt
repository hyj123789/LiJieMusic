package com.example.dynamics.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.dynamics.R
import com.example.dynamics.network.bean.Event
import com.example.dynamics.ui.theme.AccentGreen
import com.example.dynamics.ui.theme.IconFillDark
import com.example.dynamics.ui.theme.IconFillLight
import com.example.dynamics.ui.theme.LiJieMusicTheme
import com.example.dynamics.ui.theme.TextPrimaryDark
import com.example.dynamics.ui.theme.TextPrimaryLight
import com.example.dynamics.ui.theme.TextTertiaryDark
import com.example.dynamics.ui.theme.TextTertiaryLight
import com.example.dynamics.ui.viewmodel.DynamicsViewModel
import com.example.model.Profile
import com.example.model.UserManager
import com.example.util.ToastUtil
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @description 动态UI界面
 * @author Ben
 * @date 2026/8/13 9:57
 * @email 2175801434@qq.com
 * */
@Composable
fun DynamicScreen(
    viewModel: DynamicsViewModel
) {
    val context = LocalContext.current
    val profile by UserManager.profile.collectAsStateWithLifecycle()
    val rvList by viewModel.rvList.collectAsStateWithLifecycle()
    val toastMsg by viewModel.toastMsg.collectAsStateWithLifecycle()
    // 处理 Toast 消息
    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            ToastUtil.popToast(it, context)
        }
    }
    // 初始化 ViewModel
    LaunchedEffect(Unit) {
        viewModel.init()
    }
    LiJieMusicTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .background(MaterialTheme.colors.background)
        ) {
            ProfileHeader(profile)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                items(items = rvList ?: emptyList(), key = { it.id }) { event ->
                    DynamicsItem(event)
                }
            }

        }
    }
}

@Composable
fun ProfileHeader(profile: Profile?) {
    val isDark = isSystemInDarkTheme()
    val textPrimary = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textTertiary = if (isDark) TextTertiaryDark else TextTertiaryLight
    val surface = MaterialTheme.colors.surface
    val dividerColor = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)
    val vipTagBg = if (isDark) Color(0xFF3A3320) else Color(0xFFFFF3D0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像 + VIP 徽章叠加
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = profile?.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, surface, CircleShape)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.iv_avatar)
                )
                if ((profile?.vipType ?: 0) != 0) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700), CircleShape)
                            .border(1.5.dp, surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB8860B)
                        )
                    }
                }
            }

            // 昵称
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = profile?.nickname ?: "未知用户",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            // 会员徽章
            if ((profile?.vipType ?: 0) != 0) {
                Text(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(vipTagBg, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    text = "VIP 会员",
                    fontSize = 11.sp,
                    color = Color(0xFFE09A00),
                    fontWeight = FontWeight.Medium
                )
            }

            // 签名
            Text(
                modifier = Modifier
                    .padding(top = 10.dp, start = 24.dp, end = 24.dp),
                textAlign = TextAlign.Center,
                text = profile?.signature ?: "追求卓越，成功就会在不经意间找上你~~~",
                fontSize = 13.sp,
                color = textTertiary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 关注 / 粉丝
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    value = profile?.follows ?: 0,
                    label = "关注",
                    textPrimary = textPrimary,
                    textTertiary = textTertiary
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .width(1.dp)
                        .height(28.dp)
                        .background(dividerColor)
                )
                StatItem(
                    value = profile?.followeds ?: 0,
                    label = "粉丝",
                    textPrimary = textPrimary,
                    textTertiary = textTertiary
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: Int, label: String, textPrimary: Color, textTertiary: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = textTertiary,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

/**
 * 单条动态，布局对齐 item_dynamic.xml：
 * 用户头部(头像+昵称+时间) → 文案 → 配图(有则显) → 歌曲卡(有则显) → 操作栏 → 分割线
 */
@Composable
fun DynamicsItem(event: Event) {
    val isDark = isSystemInDarkTheme()
    val textPrimary = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textTertiary = if (isDark) TextTertiaryDark else TextTertiaryLight
    val iconTint = if (isDark) IconFillDark else IconFillLight
    val dividerColor = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)
    val songCardBg = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // ---- 用户头部  ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = event.user.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.iv_avatar)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = event.user.nickname,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = textPrimary
                )
                Text(
                    text = formatTimestamp(event.eventTime),
                    fontSize = 12.sp,
                    color = textTertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // ---- 动态文字  ----
        Text(
            text = event.json.msg,
            fontSize = 15.sp,
            color = textPrimary,
            lineHeight = 21.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        // ---- 动态配图 ，有则显 ----
        if (event.pics.isNotEmpty()) {
            AsyncImage(
                model = event.pics[0].originUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentScale = ContentScale.FillWidth
            )
        }

        // ---- 歌曲卡片 ，无歌则隐藏 ----
        event.json.song?.let { song ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = songCardBg
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = song.album.picUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = song.name,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = textPrimary
                        )
                        Text(
                            text = song.artists.joinToString("|") { it.name },
                            fontSize = 13.sp,
                            color = textTertiary,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // ---- 操作栏 点赞，分享，评论 ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            ActionItem(
                icon = R.drawable.ic_like,
                label = "${event.info.likedCount}",
                tint = if (event.info.liked) AccentGreen else iconTint
            )
            Spacer(Modifier.weight(1f))
            ActionItem(R.drawable.ic_comment, "${event.info.commentCount}", tint = iconTint)
            Spacer(Modifier.weight(1f))
            ActionItem(R.drawable.ic_share, "${event.info.shareCount}", tint = iconTint)
            Spacer(Modifier.weight(1f))
        }

        // ---- 底部分割线 ----
        Divider(
            modifier = Modifier.padding(top = 12.dp),
            thickness = 0.5.dp,
            color = dividerColor
        )
    }
}

/** 操作栏单项：图标 + 计数 */
@Composable
private fun ActionItem(icon: Int, label: String, tint: Color = IconFillLight) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = tint
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = tint,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

/** 毫秒时间戳 → 格式化时间 */
private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val zone = TimeZone.of("Asia/Shanghai")
    val localDateTime = instant.toLocalDateTime(zone)
    return "${localDateTime.year}-${localDateTime.monthNumber}-${localDateTime.dayOfMonth} " +
            "${localDateTime.hour}:${localDateTime.minute}:${localDateTime.second}"
}
