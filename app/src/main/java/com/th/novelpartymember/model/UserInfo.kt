package com.th.novelpartymember.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class UserInfo(
    val nickName: String = "",
    val email: String = "",
    val createdAt: Date? = null
) : Parcelable
