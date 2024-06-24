package com.th.novelpartymember.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager

open class BaseDialog(context: Context) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun resizeDialog(layoutParams: WindowManager.LayoutParams?) {
        if (layoutParams != null) {
            window!!.setAttributes(layoutParams)
        }
    }
}
