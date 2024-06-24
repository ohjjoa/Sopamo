package com.th.novelpartymember.view.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.th.novelpartymember.R
import com.th.novelpartymember.databinding.ActivityOnboardingBinding
import com.th.novelpartymember.model.OnBoardingItem
import com.th.novelpartymember.view.login.LoginActivity
import com.th.novelpartymember.view.sign_up.SignUpActivity
import java.util.Timer
import java.util.TimerTask

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private var currentPage = 0
    private var timer: Timer? = null
    private val DELAY_MS: Long = 3000 // 페이지 전환 간격
    private val PERIOD_MS: Long = 3000 // 페이지 전환 주기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.apply {
            btnSignUp.setOnClickListener {
                startActivity(Intent(this@OnBoardingActivity, SignUpActivity::class.java))
            }

            layoutLogin.setOnClickListener {
                startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
            }
        }

        val items = listOf(
            OnBoardingItem(R.drawable.img_intro_first, "다양한\n창작 소설물을\n볼 수 있어요!"),
            OnBoardingItem(R.drawable.img_intro_second, "누구나\n쉽게 소설을\n쓸 수 있어요!"),
            OnBoardingItem(R.drawable.img_intro_third, "내가\n쓴 글들을\n한번에 볼 수 있어요!"),
            OnBoardingItem(R.drawable.img_intro_fourth, "나도\n쉽게 작가로\n등록이 가능해요!")
        )

        val adapter = OnBoardingAdapter(items)
        binding.viewPagerOnboarding.adapter = adapter

        startAutoScroll()
    }

    private fun startAutoScroll() {
        val handler = android.os.Handler()
        val update = Runnable {
            if (currentPage == 4) {
                currentPage = 0
            }
            binding.viewPagerOnboarding.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // 액티비티가 종료될 때 타이머 중지
    }
}