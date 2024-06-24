package com.th.novelpartymember.view.sign_up

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.th.novelpartymember.MainActivity
import com.th.novelpartymember.custom_view.SimpleTextWatcher
import com.th.novelpartymember.databinding.ActivitySignUpBinding
import com.th.novelpartymember.utils.VariousUtils

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private var firebaseAuth: FirebaseAuth = getInstance()

    private var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.apply {
            titleView.setOnClickListener {
                finish()
            }

            btnSignUp.setOnClickListener {
                val userEmail = binding.editEmail.text.toString().trim()
                val userPassword = binding.editPassword.text.toString().trim()
                val userRePassword = binding.editRePassword.text.toString().trim()
                val userNickName = binding.editNickName.text.toString().trim()

                var isValid = true

                if (userEmail.isEmpty()) {
                    textErrorEmail.visibility = View.VISIBLE
                    isValid = false
                } else {
                    textErrorEmail.visibility = View.GONE
                }

                if (userPassword.isEmpty()) {
                    textErrorPassword.visibility = View.VISIBLE
                    isValid = false
                } else {
                    textErrorPassword.visibility = View.GONE
                }

                if (userRePassword.isEmpty()) {
                    textErrorRePassword.visibility = View.VISIBLE
                    isValid = false
                } else {
                    textErrorRePassword.visibility = View.GONE
                }

                if (userNickName.isEmpty()) {
                    textErrorNickName.visibility = View.VISIBLE
                    isValid = false
                } else {
                    textErrorNickName.visibility = View.GONE
                }

                if (isValid) {
                    checkNicknameExists(userNickName) { exists ->
                        if (exists) {
                            Toast.makeText(this@SignUpActivity, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            signUpUser(userEmail, userPassword, userNickName)
                        }
                    }
                }
            }

            editEmail.apply {
                addTextChangedListener(SimpleTextWatcher { text, _, _, _ ->
                    if (VariousUtils.isValidEmail(text)) {
                        textErrorEmail.visibility = View.GONE
                    } else {
                        textErrorEmail.visibility = View.VISIBLE
                        textErrorPassword.text = "이메일 형식에 맞게 입력해 주세요."
                    }
                })
            }

            editPassword.apply {
                addTextChangedListener(SimpleTextWatcher { text, _, _, _ ->
                    if (!text.isNullOrBlank() && text.length >= 6) {
                        textErrorPassword.visibility = View.GONE
                    } else {
                        textErrorPassword.visibility = View.VISIBLE
                        textErrorPassword.text = "비밀번호는 최소 6자 이상이어야 합니다."
                    }
                })
            }

            editRePassword.apply {
                addTextChangedListener(SimpleTextWatcher { text, _, _, _ ->
                    // 비밀번호와 재입력 비밀번호가 같은지 확인
                    if (text.toString() == editPassword.text.toString()) {
                        textErrorRePassword.visibility = View.GONE
                    } else {
                        textErrorRePassword.visibility = View.VISIBLE
                        textErrorRePassword.text = "비밀번호가 일치하지 않습니다."
                    }
                })
            }

            imgPasswordBlind.setOnClickListener {
                if (editPassword.transformationMethod is PasswordTransformationMethod) {
                    editPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
                editPassword.setSelection(editPassword.text.length)
            }

            imgRePasswordBlind.setOnClickListener {
                if (editRePassword.transformationMethod is PasswordTransformationMethod) {
                    editRePassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    editRePassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
                editRePassword.setSelection(editRePassword.text.length)
            }
        }
    }

    private fun checkNicknameExists(nickName: String, callback: (Boolean) -> Unit) {
        fireStore.collection("users")
            .whereEqualTo("nickName", nickName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    callback(!documents.isEmpty)
                } else {
                    Toast.makeText(this, "닉네임 중복 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }
    }

    private fun signUpUser(email: String, password: String, nickName: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid

                    // Firestore에 닉네임과 이메일 저장
                    if (uid != null) {
                        val userInfo = mapOf(
                            "nickName" to nickName,
                            "email" to email,
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                        fireStore.collection("users").document(uid)
                            .set(userInfo)
                            .addOnSuccessListener {
                                Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                startActivity(
                                    Intent(
                                        this@SignUpActivity,
                                        MainActivity::class.java
                                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@SignUpActivity, "회원가입 실패: $e", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this@SignUpActivity, "이미 가입 되어있는 파티원 입니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
