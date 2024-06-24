package com.th.novelpartymember.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllEpisode(
    val content: String = "",
    val episode: Long = 0,
    val subTitle: String = "",
    val title: String = "",
    val createDate: String = "",
    val nickName: String = "",
    val imageUrl: String = "",
) : Parcelable
