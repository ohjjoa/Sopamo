package com.th.novelpartymember.view.dialog

import android.content.Context
import android.view.LayoutInflater
import com.th.novelpartymember.databinding.DialogBookContentDetailBinding

class BookContentDetailDialog(context: Context) : BaseWidthNinetyPercentDialog(context) {

    private val binding: DialogBookContentDetailBinding =
        DialogBookContentDetailBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        setContentView(binding.root)

        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    fun setMessage(msg: String) : BookContentDetailDialog {
        binding.textContent.text = msg
        return this
    }
}
