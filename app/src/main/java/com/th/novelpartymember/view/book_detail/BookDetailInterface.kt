package com.th.novelpartymember.view.book_detail

import com.th.novelpartymember.model.CommentData

interface BookDetailInterface {
    fun onLikeButtonClickListener(comment: CommentData)

    fun moveReplyListener(comment: CommentData)
}