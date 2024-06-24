package com.th.novelpartymember.view.my_page

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.th.novelpartymember.databinding.ActivityMyPageManagementBinding

class MyPageManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMyPageManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.apply {
            titleView.setOnClickListener {
                finish()
            }
        }
    }
}