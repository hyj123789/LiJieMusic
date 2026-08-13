package com.example.login.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.ui.viewmodel.LoginViewModel
import com.example.login.R
import com.example.login.ui.theme.AccentGreen
import com.example.login.ui.theme.LiJieMusicTheme

@Composable
fun ScanScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val qrBitmap by viewModel.qrBitmap.collectAsState()
    val codeStatus by viewModel.codeStatus.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

    LiJieMusicTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // 标题
            Text(
                text = "扫码登录",
                color = MaterialTheme.colors.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 状态文字
            Text(
                text = codeStatus ?: "点击下方按钮获取二维码",
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 二维码卡片（1:1 比例）
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 二维码图片
                    qrBitmap?.let { bitmap: Bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "二维码",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // 扫描框四角装饰
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                            .background(
                                color = AccentGreen,
                                shape = RoundedCornerShape(topStart = 4.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_scan_corner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .rotate(90f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_scan_corner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomStart)
                            .rotate(270f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_scan_corner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd)
                            .rotate(180f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_scan_corner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 底部提示
            Text(
                text = "使用网易云音乐 App\n或微信「扫一扫」登录",
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.55f),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 刷新按钮
            Button(
                onClick = { viewModel.getQrCode() },
                colors = ButtonDefaults.buttonColors(backgroundColor = AccentGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "获取或刷新二维码", fontSize = 15.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
