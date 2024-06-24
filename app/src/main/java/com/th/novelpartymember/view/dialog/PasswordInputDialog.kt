package com.th.novelpartymember.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.th.novelpartymember.databinding.DialogPasswordInputBinding

class PasswordInputDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogPasswordInputBinding
    private var onConfirmClickListener: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPasswordInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener {
            val password = binding.etPassword.text.toString()
            if (password.isNotEmpty()) {
                onConfirmClickListener?.invoke(password)
                dismiss()
            } else {
                binding.etPassword.error = "비밀번호를 입력하세요"
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnConfirmClickListener(listener: (String) -> Unit) {
        onConfirmClickListener = listener
    }
}
