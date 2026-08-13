package com.example.login.ui.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import com.example.login.ui.viewmodel.LoginViewModel
import com.example.login.R
import com.example.login.ui.theme.AccentGreen
import com.example.login.ui.theme.LiJieMusicTheme
import com.example.util.ToastUtil
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * @description 手机号登录界面的UI
 * @author Ben
 * @date 2026/8/12 22:48
 * @mail 2175801434@qq.com
 * */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMail: () -> Unit, //点击邮箱登录跳转
    onNavigateToScan: () -> Unit, //点击二维码登录跳转
    onGuestLogin: () -> Unit,     //游客登录跳转
    onLoginSuccess: () -> Unit    //登录成功跳转主界面
) {
    val context = LocalContext.current

    var phone by remember { mutableStateOf("") }
    var captcha by remember { mutableStateOf("") }

    // 验证码倒计时（替换原 CountDownTimer）
    var countdown by remember { mutableIntStateOf(0) }
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1.seconds)
            countdown--
        }
    }

    // 收集 toast 消息
    val toastMsg by viewModel.toastMsg.collectAsState()
    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            ToastUtil.popToast(it, context)
        }
    }

    // 收集登录成功
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

    LiJieMusicTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // —— 主内容（可滚动）——
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                // 品牌标题
                Text(
                    text = "LiJie Music",
                    color = AccentGreen,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(56.dp))

                // 手机号输入框
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("请输入手机号") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_phone),
                            contentDescription = "手机号",
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        cursorColor = MaterialTheme.colors.primary,
                        focusedBorderColor = MaterialTheme.colors.primary,
                        unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
                        focusedLabelColor = MaterialTheme.colors.primary,
                        unfocusedLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 验证码输入框 + 获取按钮（水平排列）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = captcha,
                        onValueChange = { captcha = it },
                        label = { Text("请输入验证码") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_captcha),
                                contentDescription = "验证码",
                                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = MaterialTheme.colors.onSurface,
                            cursorColor = MaterialTheme.colors.primary,
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
                            focusedLabelColor = MaterialTheme.colors.primary,
                            unfocusedLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (phone.isBlank()) {
                                ToastUtil.popToastLong(
                                    "wochaowei,没写号码你获取什么验证码",
                                    context
                                )
                                return@Button
                            }
                            viewModel.sendCaptcha(phone)
                            countdown = 60
                        },
                        enabled = countdown == 0,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AccentGreen,
                            disabledBackgroundColor = AccentGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text(
                            text = if (countdown > 0) "${countdown}s 后重试" else "获取验证码",
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 登录按钮
                Button(
                    onClick = { viewModel.loginByPhone(phone, captcha) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AccentGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(text = "登  录", fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(56.dp))

                // "其他登录方式" 横线分隔
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.55f),
                        thickness = 1.dp
                    )
                    Text(
                        text = "其他登录方式",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.55f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.55f),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 其他登录图标（邮箱 / 扫码）
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircleIconButton(
                        drawableRes = R.drawable.ic_mail,
                        contentDescription = "邮箱登录",
                        onClick = onNavigateToMail
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    CircleIconButton(
                        drawableRes = R.drawable.ic_scan,
                        contentDescription = "扫码登录",
                        onClick = onNavigateToScan
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 注册链接（无点击逻辑，与原 XML 一致）
                Text(
                    text = "还没有账号？请点击这里",
                    color = AccentGreen,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { /* 原 XML 也无点击逻辑 */ }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            // —— 右上角"游客登录"（绝对位置）——
            Text(
                text = "游客登录请点这里哇",
                color = MaterialTheme.colors.onSurface,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .clickable { onGuestLogin() }
            )
        }
    }
}

/** 圆形图标登录按钮（替代原 CardView > ImageView） */
@Composable
private fun CircleIconButton(
    drawableRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = drawableRes),
            contentDescription = contentDescription,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(28.dp)
        )
    }
}