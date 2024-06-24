package com.th.novelpartymember.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotifyData(
    val content: String = "",
    val title: String = "",
    val date: String = ""
) : Parcelable