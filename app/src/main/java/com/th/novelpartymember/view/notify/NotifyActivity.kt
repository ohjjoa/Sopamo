package com.th.novelpartymember.view.notify

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.th.novelpartymember.databinding.ActivityNotifyBinding
import com.th.novelpartymember.enums.title.TitleMode
import com.th.novelpartymember.model.NotifyData

class NotifyActivity : AppCompatActivity(), NotifyItemInterface {
    private lateinit var binding: ActivityNotifyBinding

    private lateinit var notifyAdapter: NotifyAdapter

    override fun notifyItemClickListener(notifyData: NotifyData) {
        val intent = Intent(this, NotifyDetailActivity::class.java)
        intent.putExtra("notifyData", notifyData)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        binding.apply {
            titleView.setOnClickListener {
                finish()
            }
            titleView.setMode(TitleMode.BLACK_BACK, this@NotifyActivity)

            notifyAdapter = NotifyAdapter(this@NotifyActivity)

            recyclerNotify.apply {
                layoutManager = LinearLayoutManager(this@NotifyActivity)
                adapter = notifyAdapter
            }
        }
    }
}