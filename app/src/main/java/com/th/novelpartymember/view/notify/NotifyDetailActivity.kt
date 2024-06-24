package com.th.novelpartymember.view.notify

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.th.novelpartymember.databinding.ActivityNotifyDetailBinding
import com.th.novelpartymember.model.NotifyData

class NotifyDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotifyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNotifyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.apply {
            titleView.setOnClickListener {
                finish()
            }

            // Intent에서 NotifyData 객체를 받음
            val notifyData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("notifyData", NotifyData::class.java)
            } else {
                intent.getParcelableExtra("notifyData")
            }

            // notifyData가 null이 아닌 경우 UI에 데이터를 설정
            notifyData?.let {
                binding.apply {
                    titleView.setTitleView(it.title)
                    textDate.text = it.date
                    textContent.text = it.content
                }
            }
        }
    }
}