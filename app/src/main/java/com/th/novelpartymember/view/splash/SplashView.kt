package com.th.novelpartymember.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.th.novelpartymember.MainActivity
import com.th.novelpartymember.R
import com.th.novelpartymember.view.onboarding.OnBoardingActivity
import com.th.novelpartymember.view.sign_up.SignUpActivity
import com.th.novelpartymember.view.splash.ui.theme.NovelPartyMemberTheme

class SplashView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NovelPartyMemberTheme {
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current

    val splashViewModel: SplashViewModel = viewModel()

    var isLoading by remember {
        mutableStateOf(true)
    }

    // 스플래시 화면을 3초 동안 보여줍니다.
    LaunchedEffect(Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            isLoading = false
            if (splashViewModel.inUserLoggedIn()) {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, OnBoardingActivity::class.java)
                context.startActivity(intent)
            }
        }, 3000) // 3초 후
    }

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize() // 전체 화면을 채우도록 변경
                .background(color = Color.Black),
            verticalArrangement = Arrangement.Center, // 수직 중앙 정렬
            horizontalAlignment = Alignment.CenterHorizontally // 수평 중앙 정렬
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_sopamo_text_white),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
            )
            Text(
                text = "소설쓰는 파티원 모집",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp) // 텍스트와 이미지 간의 간격
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NovelPartyMemberTheme {
        SplashScreen()
    }
}