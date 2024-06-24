package com.th.novelpartymember.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.th.novelpartymember.databinding.DialogLogoutBinding

class LogoutDialog(context: Context) : BaseWidthNinetyPercentDialog(context) {

    private val binding: DialogLogoutBinding = DialogLogoutBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        setContentView(binding.root)
    }

    fun setMessage(msg: String) : LogoutDialog {
        binding.textTitle.text = msg
        return this
    }

    fun setOnConfirmClick(confirmClick: View.OnClickListener): LogoutDialog {
        binding.buttonConfirm.setOnClickListener(confirmClick)
        return this
    }

    fun setOnCancelClick(cancelClick: View.OnClickListener): LogoutDialog {
        binding.buttonCancel.setOnClickListener(cancelClick)
        return this
    }
}
