package com.th.novelpartymember.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentData(
    val userId: String = "",
    val nickName: String = "",
    val comment: String = "",
    val date: String = "",
    var likes: Int = 0,
    var likedBy: MutableList<String> = mutableListOf(),
    var replies: MutableList<CommentData> = mutableListOf(),
    var replyCount: Int = 0
) : Parcelable