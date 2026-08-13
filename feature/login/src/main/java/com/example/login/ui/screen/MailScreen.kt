package com.example.login.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.login.ui.viewmodel.LoginViewModel
import com.example.login.R
import com.example.login.ui.theme.AccentGreen
import com.example.login.ui.theme.LiJieMusicTheme

@Composable
fun MailScreen(
    viewModel: LoginViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LiJieMusicTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            // 品牌标题
            Text(
                text = "LiJie Music",
                color = AccentGreen,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 副标题
            Text(
                text = "网易邮箱账号登录",
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(56.dp))

            // 邮箱输入框
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("请输入网易邮箱地址") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mail),
                        contentDescription = "邮箱",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            Spacer(modifier = Modifier.height(24.dp))

            // 密码输入框（带显示/隐藏切换）
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("请输入密码") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_captcha),
                        contentDescription = "密码",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

            Spacer(modifier = Modifier.height(48.dp))

            // 登录按钮
            Button(
                onClick = { viewModel.loginByMail() },
                colors = ButtonDefaults.buttonColors(backgroundColor = AccentGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = "登  录", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
