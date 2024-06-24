package com.th.novelpartymember.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserWriteData(
    val content: String = "",
    val episode: Long = 1,
    val subTitle: String = "",
    val title: String = ""
) : Parcelable