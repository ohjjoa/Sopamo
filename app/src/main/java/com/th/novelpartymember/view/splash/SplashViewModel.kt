package com.th.novelpartymember.view.splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashViewModel: ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    fun inUserLoggedIn() : Boolean {
        return auth.currentUser != null
    }
}