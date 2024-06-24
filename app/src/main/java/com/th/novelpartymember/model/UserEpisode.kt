package com.th.novelpartymember.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEpisode(
    val content: String = "",
    val episode: Long = 0,
    val subTitle: String = "",
    val title: String = "",
    val createDate: String = "",
    val nickName: String = "",
    val imageUrl: String = "",
) : Parcelable
