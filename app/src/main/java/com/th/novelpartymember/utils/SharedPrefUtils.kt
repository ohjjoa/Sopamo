package com.th.novelpartymember.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefUtils {
    private const val PREF_NAME = "PREF_FILE"
    private const val SHARED_KEY_AUTO_LOGIN = "autoLogin"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun putAutoLogin(context: Context, isAutoLogin: Boolean) {
        getSharedPreferences(context).edit()
            .putBoolean(SHARED_KEY_AUTO_LOGIN, isAutoLogin)
            .apply()
    }

    fun getIsAutoLogin(context: Context, defaultValue: Boolean): Boolean {
        return getSharedPreferences(context).getBoolean(SHARED_KEY_AUTO_LOGIN, defaultValue)
    }
}
