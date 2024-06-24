package com.th.novelpartymember.view.dialog

import android.content.Context
import android.view.WindowManager
import com.th.novelpartymember.utils.ViewUtils

class LayoutParamBuilder(
    private val layoutParams: WindowManager.LayoutParams,
    private val context: Context
) {
    fun setWidth(percent: Float): LayoutParamBuilder {
        layoutParams.width = ((ViewUtils.getDeviceMetrics(context).widthPixels * percent).toInt())
        return this
    }

    fun setHeight(percent: Float): LayoutParamBuilder {
        layoutParams.height = ((ViewUtils.getDeviceMetrics(context).heightPixels * percent).toInt())
        return this
    }

    fun setHeight(percent: Double): LayoutParamBuilder {
        layoutParams.height = ((ViewUtils.getDeviceMetrics(context).heightPixels * percent).toInt())
        return this
    }

    fun setGravity(gravity: Int): LayoutParamBuilder {
        layoutParams.gravity = gravity
        return this
    }

    fun build(): WindowManager.LayoutParams {
        return layoutParams
    }
}
