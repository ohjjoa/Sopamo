package com.th.novelpartymember.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.th.novelpartymember.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.th.novelpartymember.databinding.ActivitySplashBinding
import com.th.novelpartymember.view.onboarding.OnBoardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        init()
    }
    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            checkUser()
        }, 3000) // 4초 후에 다음 화면으로 이동
    }

    private fun checkUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        startActivity(Intent(this, OnBoardingActivity::class.java))
        finish()
    }
}