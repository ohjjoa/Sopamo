package com.th.novelpartymember

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.th.novelpartymember.model.EpisodeLikes

class BookInfoViewModel : ViewModel() {

    private val _episodeLikes = MutableLiveData<EpisodeLikes>()

    private val _commentCount = MutableLiveData<Int>()

    val episodeLikes: LiveData<EpisodeLikes> get() = _episodeLikes

    fun setEpisodeLikes(likes: EpisodeLikes) {
        _episodeLikes.value = likes
    }

    fun updateEpisodeLikes(likes: EpisodeLikes) {
        _episodeLikes.postValue(likes)
    }

    fun getEpisodeLikes(): MutableLiveData<EpisodeLikes> {
        return _episodeLikes
    }

    val commentCount: LiveData<Int> get() = _commentCount

    fun setCommentCount(count: Int) {
        _commentCount.value = count
    }
}