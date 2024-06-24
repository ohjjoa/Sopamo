package com.th.novelpartymember.view.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.th.novelpartymember.MainActivity
import com.th.novelpartymember.custom_view.SimpleTextWatcher
import com.th.novelpartymember.databinding.ActivityLoginBinding
import com.th.novelpartymember.utils.VariousUtils.isValidEmail

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    // TODO: 추후 구글 sns 활성화시 사용
//    private lateinit var googleSignInClient: GoogleSignInClient
    private var firebaseAuth: FirebaseAuth = getInstance()
    private val firebaseAuthListener: AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        // TODO: 추후 구글 sns 활성화시 사용
        //
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.apply {
            titleView.setOnClickListener {
                finish()
            }

            btnLogin.setOnClickListener {
                val userEmail = binding.editEmail.text.toString().trim()
                val userPassword = binding.editPassword.text.toString().trim()

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

                if (isValid) {
                    loginUser(userEmail, userPassword)
                }
            }

            editEmail.apply {
                addTextChangedListener(SimpleTextWatcher { text, _, _, _ ->
                    if (isValidEmail(text)) {
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

            imgEmailClear.setOnClickListener {
                editEmail.setText("")
            }

            imgPasswordBlind.setOnClickListener {
                if (editPassword.transformationMethod is PasswordTransformationMethod) {
                    editPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
                editPassword.setSelection(editPassword.text.length)
            }

            // TODO: 추후 구글 sns 활성화시 사용
//            imageGoogle.setOnClickListener {
//                startGoogleSignIn()
//            }
        }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this@LoginActivity
            ) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    firebaseAuthListener?.let { authStateListener ->
                        firebaseAuth.addAuthStateListener(authStateListener)
                    }
                    val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(
                        currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )

                    startActivity(
                        Intent(this@LoginActivity,
                            MainActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                } else {
                    // 로그인 실패, 통신
                    Toast.makeText(
                        this@LoginActivity,
                        "에러 발생",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener)
        }
    }

    // TODO: 추후 구글 sns 활성화시 사용
//    private val startGoogleSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            // 처리할 코드 추가
//            handleGoogleSignInResult(data)
//        } else {
//            // Google Sign-In 실패 처리
//            Toast.makeText(this, "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

    // TODO: 추후 구글 sns 활성화시 사용
//    private fun startGoogleSignIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startGoogleSignIn.launch(signInIntent)
//    }

    // TODO: 추후 구글 sns 활성화시 사용
//    private fun handleGoogleSignInResult(data: Intent?) {
//        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//        try {
//            // Google Sign-In 성공
//            val account = task.getResult(ApiException::class.java)
//            firebaseAuthWithGoogle(account?.idToken!!)
//        } catch (e: ApiException) {
//            // Google Sign-In 실패
//            Toast.makeText(this@LoginActivity, "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
    // TODO: 추후 구글 sns 활성화시 사용
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // 로그인 성공
//                    val user = firebaseAuth.currentUser
//                    // 추가적인 작업을 수행할 수 있습니다.
//                    print(user)
//                } else {
//                    // 로그인 실패
//                    Toast.makeText(this, "Firebase에 Google 사용자 인증 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
}
