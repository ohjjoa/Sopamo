package com.th.novelpartymember

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.th.novelpartymember.model.UserInfo
import java.util.*

class UserInfoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo>
        get() = _userInfo

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // 오류 처리
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val nickname = snapshot.getString("nickName") ?: ""
                    val email = snapshot.getString("email") ?: ""
                    val createdAtTimestamp = snapshot.getTimestamp("createdAt")
                    val createdAt = createdAtTimestamp?.toDate() ?: Date()

                    val userInfo = UserInfo(nickname, email, createdAt)
                    _userInfo.postValue(userInfo)
                }
            }
        }
    }

    fun setNickName(newNickName: String) {
        _userInfo.value?.let {
            _userInfo.value = it.copy(nickName = newNickName)
        }
    }
}
