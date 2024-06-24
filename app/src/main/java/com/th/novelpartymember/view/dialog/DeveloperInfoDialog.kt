package com.th.novelpartymember.view.dialog

import android.content.Context
import com.th.novelpartymember.databinding.DialogDeveloperInfoBinding

class DeveloperInfoDialog(context: Context, content: String?) :
    BaseWidthNinetyPercentDialog(context) {
    private val binding: DialogDeveloperInfoBinding = DialogDeveloperInfoBinding.inflate(
        layoutInflater
    )

    init {
        setContentView(binding.getRoot())
        binding.textCheck.setOnClickListener { dismiss() }
    }
}
