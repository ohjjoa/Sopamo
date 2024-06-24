package com.th.novelpartymember.view.my_page

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.th.novelpartymember.UserInfoViewModel
import com.th.novelpartymember.databinding.ActivityMyPageProfileChangeBinding

class MyPageProfileChangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageProfileChangeBinding

    private var firebaseAuth: FirebaseAuth = getInstance()

    private var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var userInfoViewModel: UserInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyPageProfileChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userInfoViewModel = ViewModelProvider(this@MyPageProfileChangeActivity)[UserInfoViewModel::class.java]

        init()
    }

    private fun init() {
        binding.apply {
            titleView.setOnClickListener {
                finish()
            }

            buttonConfirm.setOnClickListener {
                val newNickName = editChangeNickName.text.toString()
                if (newNickName.isNotEmpty()) {
                    updateNickName(newNickName)
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra("newNickName", newNickName)
                    })
                    finish()
                } else {
                    Toast.makeText(this@MyPageProfileChangeActivity, "Please enter a nickname", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateNickName(newNickName: String) {
        val user = firebaseAuth.currentUser
        val uid = user?.uid

        if (uid != null) {
            fireStore.collection("users").document(uid)
                .update("nickName", newNickName)
                .addOnSuccessListener {
                    userInfoViewModel.setNickName(newNickName)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@MyPageProfileChangeActivity, "Error updating nickname: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}