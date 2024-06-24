package com.th.novelpartymember.model

data class EpisodeLikes(
    var episodeLikes: Int = 0,
    var episodeLikedBy: MutableList<String> = mutableListOf()
)