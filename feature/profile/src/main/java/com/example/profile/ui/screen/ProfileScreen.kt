package com.example.profile.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.Profile
import com.example.model.UserManager
import com.example.profile.R
import com.example.profile.network.bean.Creator
import com.example.profile.network.bean.Playlist
import com.example.profile.ui.theme.LiJieMusicTheme
import com.example.profile.ui.viewmodel.ProfileViewModel


/**
 * @author Ben
 * @date 2026/8/13 18:27
 * @description 主页的UI，用户的信息+歌单
 * */
@Composable
fun ProfileScreen(viewModel: ProfileViewModel ,onPlaylistClick: (String) -> Unit) {
    val list by viewModel.listData.collectAsStateWithLifecycle()
    val profile by UserManager.profile.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        viewModel.loadPlaylist()
    }
    LiJieMusicTheme {
        // 获取当前主题的颜色
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderLayout(profile)
            Divider(thickness = 2.dp)
            // 过渡区域：分类 Tab + 快捷操作
            PlaylistTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onCreateClick = { /* TODO: 创建的歌单 */ },
                onCollectClick = { /* TODO: 收藏的歌单 */ }
            )
            LazyColumn(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            ) {
                items(items = list ?: emptyList(), key = { it.id }) { playList ->
                    PlaylistItem(
                        playlist = playList,
                        nickname = profile?.nickname ?: "null",
                        onPlaylistClick
                    )
                }
            }
        }
    }
}

// 歌单分类过渡区域：Tab 切换 + 播放全部
@Composable
private fun PlaylistTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onCreateClick: () -> Unit,
    onCollectClick: () -> Unit
) {
    val tabs = listOf("创建", "收藏")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左：分类 Tab
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Column(
                        modifier = Modifier.clickable {
                            onTabSelected(index)
                            if (index == 0) onCreateClick() else onCollectClick()
                        },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = if (selectedTab == index) 16.sp else 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.Black else Color.Gray
                        )
                        // 选中下划线
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(width = 24.dp, height = 3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (selectedTab == index) Color(0xFFFF3A3A)
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            // 右：更多按钮
            Image(
                painter = painterResource(R.drawable.ic_more),
                contentDescription = "更多",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* TODO: 更多 */ }
            )
        }
    }
}

//header 头布局，介绍基础信息
@Composable
fun HeaderLayout(profile: Profile?) {
    // ❌ 问题代码：rememberAsyncImagePainter 加载后带有图片原始固有高度，
    // 配合 .paint() 会把 Box 拉高到图片原始尺寸，导致 header 异常撑高
    // val painter = rememberAsyncImagePainter(
    //     model = ImageRequest.Builder(LocalContext.current).data(profile?.backgroundUrl).build()
    // )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
        // .paint(
        //     painter = painter, contentScale = ContentScale.Crop
        // )
    ) {
        // ✅ 修复：matchParentSize 让背景跟随 Box，不参与测量
        // 背景图 + 底部半透明遮罩（保证文字在亮色背景图上也可读）
        AsyncImage(
            model = profile?.backgroundUrl,
            contentDescription = "背景",
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize(),
            contentScale = ContentScale.Crop
        )
        // 渐变遮罩：从透明到底部黑色 30%，适配亮/暗色背景图
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x4D000000))
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像：白色圆形边框包裹
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(3.dp)
            ) {
                AsyncImage(
                    model = profile?.avatarUrl,
                    placeholder = painterResource(R.drawable.iv_avatar),
                    contentDescription = "头像",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(50)),
                )
            }

            // 昵称 + 会员标签 同一行
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = profile?.nickname ?: "未知用户",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (profile?.vipType != 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .background(
                                color = Color(0x40FFFFFF),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VIP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // 简介
            Text(
                text = profile?.signature ?: "追求卓越，成功就会在不经意间找上你~~~",
                fontSize = 13.sp,
                color = Color(0xDDFFFFFF),
                modifier = Modifier.padding(top = 6.dp)
            )

            // 功能入口
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileMenuItem(
                    iconRes = R.drawable.ic_recent,
                    label = "最近",
                    onClick = { /* TODO */ }
                )
                ProfileMenuItem(
                    iconRes = R.drawable.ic_local,
                    label = "本地",
                    onClick = { /* TODO */ }
                )
                ProfileMenuItem(
                    iconRes = R.drawable.ic_vip,
                    label = "会员",
                    onClick = { /* TODO */ }
                )
                ProfileMenuItem(
                    iconRes = R.drawable.ic_dressup,
                    label = "装扮",
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}
//最近、本地、会员、装扮复用UI
@Composable
private fun ProfileMenuItem(
    @DrawableRes iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

//每个歌单的子item
@Composable
fun PlaylistItem(playlist: Playlist, nickname: String,onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 10.dp)
            .clickable(true){ onClick.invoke(playlist.id.toString()) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp)),
            model = playlist.coverImgUrl,
            contentDescription = "歌单封面",
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = playlist.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "歌单|${playlist.trackCount}首|$nickname",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_more),
            contentDescription = "更多",
            modifier = Modifier.size(24.dp)
        )
    }
}

//下面三个是预览函数
@Preview(showBackground = true, name = "Header - 浅色")
@Composable
private fun HeaderLayoutPreview() {
    LiJieMusicTheme {
        HeaderLayout(
            profile = Profile(
                userId = 1L,
                nickname = "张三",
                avatarUrl = "",
                backgroundUrl = null,
                gender = 1,
                signature = "追求卓越，成功就会在不经意间找上你~~~",
                followed = false,
                followeds = 100,
                follows = 50,
                vipType = 1
            ),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Header - 深色")
@Composable
private fun HeaderLayoutDarkPreview() {
    LiJieMusicTheme {
        HeaderLayout(
            profile = Profile(
                userId = 1L,
                nickname = "张三",
                avatarUrl = "",
                backgroundUrl = null,
                gender = 1,
                signature = "追求卓越，成功就会在不经意间找上你~~~",
                followed = false,
                followeds = 100,
                follows = 50,
                vipType = 1
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaylistTabBarPreview() {
    LiJieMusicTheme {
        PlaylistTabBar(
            selectedTab = 0,
            onTabSelected = {},
            onCreateClick = {},
            onCollectClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaylistItemPreview() {
    LiJieMusicTheme {
        PlaylistItem(
            playlist = Playlist(
                adType = 0,
                anonimous = false,
                artists = Any(),
                backgroundCoverId = 0,
                backgroundCoverUrl = Any(),
                cloudTrackCount = 0,
                commentThreadId = "",
                containsTracks = true,
                copied = false,
                coverImgId = 0L,
                coverImgIdStr = "",
                coverImgUrl = "",
                createTime = 0L,
                creator = Creator(
                    accountStatus = 0,
                    anchor = false,
                    authStatus = 0,
                    authenticationTypes = 0,
                    authority = 0,
                    avatarDetail = Any(),
                    avatarImgId = 0L,
                    avatarImgIdStr = "",
                    avatarImgId_str = "",
                    avatarUrl = "",
                    backgroundImgId = 0L,
                    backgroundImgIdStr = "",
                    backgroundUrl = "",
                    birthday = 0,
                    city = 0,
                    defaultAvatar = false,
                    description = "",
                    detailDescription = "",
                    djStatus = 0,
                    expertTags = Any(),
                    experts = Any(),
                    followed = false,
                    gender = 1,
                    mutual = false,
                    nickname = "张三",
                    province = 0,
                    remarkName = Any(),
                    signature = "",
                    userId = 1L,
                    userType = 0,
                    vipType = 0
                ),
                description = "",
                englishTitle = Any(),
                highQuality = false,
                id = 1L,
                mix = false,
                name = "我喜欢的音乐",
                newImported = false,
                opRecommend = false,
                ordered = false,
                playCount = 0,
                privacy = 0,
                recommendInfo = Any(),
                shareStatus = Any(),
                sharedUsers = Any(),
                specialType = 0,
                status = 0,
                subscribed = Any(),
                subscribedCount = 0,
                subscribers = emptyList(),
                tags = emptyList(),
                titleImage = 0,
                titleImageUrl = Any(),
                top = false,
                totalDuration = 0,
                trackCount = 128,
                trackNumberUpdateTime = 0L,
                trackUpdateTime = 0L,
                tracks = Any(),
                updateFrequency = Any(),
                updateTime = 0L,
                userId = 1L
            ),
            nickname = "张三",
            onClick = {}
        )
    }
}