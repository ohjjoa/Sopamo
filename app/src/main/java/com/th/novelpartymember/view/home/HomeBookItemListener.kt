package com.th.novelpartymember.view.home

import com.th.novelpartymember.model.BookData

interface HomeBookItemListener {

    fun onReplayClickListener(bookData: BookData)

    fun onAllBookClickListener(bookData: BookData)
}