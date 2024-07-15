package com.th.novelpartymember.view.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.th.novelpartymember.R
import com.th.novelpartymember.view.login.LoginActivity
import com.th.novelpartymember.view.onboarding.ui.theme.NovelPartyMemberTheme
import com.th.novelpartymember.view.sign_up.SignUpActivity
import kotlinx.coroutines.delay

class OnBoardingView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovelPartyMemberTheme {
                OnboardingScreen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen() {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 4 })

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000) // 4초 대기
            val nextPage = (pagerState.currentPage + 1) % 4
            pagerState.animateScrollToPage(nextPage) // 여기서 직접 호출
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleSection()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f) // Take up available space
                .padding(top = 24.dp, bottom = 16.dp)
        ) { page ->
            when (page) {
                0 -> OnboardingPage(
                    description = "다양한\n창작 소설물을\n볼 수 있어요!",
                    imageRes = R.drawable.img_intro_first,
                )

                1 -> OnboardingPage(
                    description = "누구나\n쉽게 소설을\n쓸 수 있어요!",
                    imageRes = R.drawable.img_intro_second
                )

                2 -> OnboardingPage(
                    description = "내가\n" +
                            "쓴 글들을\n" +
                            "한번에 볼 수 있어요!",
                    imageRes = R.drawable.img_intro_third
                )

                3 -> OnboardingPage(
                    description = "나도\n" +
                            "쉽게 작가로\n" +
                            "등록이 가능해요!",
                    imageRes = R.drawable.img_intro_fourth
                )
            }
        }

        // Sign Up Button at the bottom
        SignUpButton {
            val intent = Intent(context, SignUpActivity::class.java)
            context.startActivity(intent)
        }

        TextLogin {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}

@Composable
fun TitleSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sopamo),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "소설쓰는 파티원 모집",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun OnboardingPage(description: String, imageRes: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        )
    }
}

@Composable
fun SignUpButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Text(text = "파티원 등록하기", color = Color.White)
    }
}

@Composable
fun TextLogin(onClick: () -> Unit) {
    Text(
        text = "파티원 이신가요??? 로그인",
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
            .clickable {
                onClick()
            },
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    NovelPartyMemberTheme {
        OnboardingScreen()
    }
}
