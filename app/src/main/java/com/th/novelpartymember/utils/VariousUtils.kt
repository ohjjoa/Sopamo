package com.th.novelpartymember.utils

import android.app.Activity
import android.graphics.Rect
import android.util.Patterns
import android.view.View
import androidx.core.widget.NestedScrollView


object VariousUtils {
    fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun setupKeyboardVisibilityListener(
        activity: Activity,
        nestedScrollView: NestedScrollView,
        focusedView: View
    ) {
        val decorView: View = activity.window.decorView
        decorView.getViewTreeObserver().addOnGlobalLayoutListener {
            val r = Rect()
            decorView.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = decorView.getRootView().height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) { // 키보드가 보임
                nestedScrollView.post {
                    nestedScrollView.smoothScrollTo(
                        0,
                        focusedView.bottom
                    )
                }
            }
        }
    }
}