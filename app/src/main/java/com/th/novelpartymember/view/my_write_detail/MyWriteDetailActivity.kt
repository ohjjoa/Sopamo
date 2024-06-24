package com.th.novelpartymember.view.my_write_detail

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.th.novelpartymember.databinding.ActivityMyWriteDetailBinding
import com.th.novelpartymember.enums.title.TitleMode
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.model.UserEpisode

class MyWriteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyWriteDetailBinding

    private var userEpisode: UserEpisode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMyWriteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        userEpisode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("userEpisode", UserEpisode::class.java)
        } else {
            intent.getParcelableExtra("bookData")
        }

        binding.apply {
            titleView.setOnClickListener {
                finish()
            }

            titleView.setMode(TitleMode.BLACK_BACK, this@MyWriteDetailActivity)

            titleView.setTitleView(userEpisode?.title + userEpisode?.episode + "화")

            textSubTitle.text = userEpisode?.subTitle
            textContent.text = userEpisode?.content
        }
    }
}