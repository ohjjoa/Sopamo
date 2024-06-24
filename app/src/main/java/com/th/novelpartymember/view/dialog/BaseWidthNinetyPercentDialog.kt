package com.th.novelpartymember.view.dialog

import android.content.Context

open class BaseWidthNinetyPercentDialog(private var context: Context) : BaseDialog(context) {
    private val NINETY_WIDTH = 0.9f
    private var height = 0.0

    override fun onStart() {
        super.onStart()
        val builder: LayoutParamBuilder = LayoutParamBuilder(window!!.attributes, context)
            .setWidth(NINETY_WIDTH)
        if (height > 0) {
            builder.setHeight(height)
            return
        }
        resizeDialog(builder.build())
    }
}
