package com.th.novelpartymember.view.my_page

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.th.novelpartymember.databinding.ActivityMyPageSettingDetailBinding
import com.th.novelpartymember.model.UserInfo
import com.th.novelpartymember.view.dialog.LogoutDialog
import com.th.novelpartymember.view.dialog.PasswordInputDialog
import com.th.novelpartymember.view.onboarding.OnBoardingActivity
import android.Manifest
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class MyPageSettingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageSettingDetailBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var profileChangeLauncher: ActivityResultLauncher<Intent>

    private var userInfo: UserInfo? = null

    private val userLogOutDialog: LogoutDialog by lazy {
        LogoutDialog(this@MyPageSettingDetailActivity)
    }

    private var photoURI: Uri? = null

    private lateinit var takePicture: ActivityResultLauncher<Uri>

    private lateinit var pickPhoto: ActivityResultLauncher<String>

    val REQUEST_PERMISSION_MIN_TIRAMISU: Int = 1005

    val REQUEST_PERMISSION_TIRAMISU: Int = 1006


    private lateinit var storageRef: StorageReference

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMyPageSettingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileChangeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val newNickName = data?.getStringExtra("newNickName")
                if (newNickName != null) {
                    binding.textUserNickName.text = newNickName
                }
            }
        }
        init()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()

        storageRef = FirebaseStorage.getInstance().reference

        firestore = FirebaseFirestore.getInstance()

        binding.apply {
            layoutNickName.setOnClickListener {
                val intent = Intent(this@MyPageSettingDetailActivity, MyPageProfileChangeActivity::class.java)
                profileChangeLauncher.launch(intent)
            }

            titleView.setOnClickListener {
                finish()
            }

            layoutSignOut.setOnClickListener {
                userLogOutDialog.setMessage("파티에서탈퇴하시겠습니까???")
                    .setOnConfirmClick { showPasswordInputDialog() }
                    .setOnCancelClick { userLogOutDialog.dismiss() }
                    .show()
            }

            userInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("userInfo", UserInfo::class.java)
            } else {
                intent.getParcelableExtra("userInfo")
            }

            userInfo?.let {
                binding.textUserNickName.text = it.nickName
                binding.textUserEmail.text = it.email
            }
        }

        loadUserImage()

        setCameraPermissionCheck()

        takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // 사진을 찍고 처리
                photoURI?.let {
                    Glide.with(this@MyPageSettingDetailActivity)
                        .load(it)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.imgUserPhoto)
                    uploadImageToFirebase(it)
                }
            }
        }

        pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // 갤러리에서 선택한 이미지를 처리할 작업
                Glide.with(this@MyPageSettingDetailActivity)
                    .load(it)
                    .apply(RequestOptions().circleCrop())
                    .into(binding.imgUserPhoto)
                uploadImageToFirebase(it)
            }
        }
    }


    private fun showPasswordInputDialog() {
        val passwordInputDialog = PasswordInputDialog(this)
        passwordInputDialog.setOnConfirmClickListener { password ->
            signOut(password)
        }
        passwordInputDialog.show()
    }

    private fun signOut(password: String) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            val email = user.email
            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, password)

                user.reauthenticate(credential)
                    .addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            user.delete()
                                .addOnCompleteListener { deleteTask ->
                                    if (deleteTask.isSuccessful) {
                                        startActivity(
                                            Intent(this@MyPageSettingDetailActivity, OnBoardingActivity::class.java)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        finish()
                                    } else {
                                        // 탈퇴 도중 에러 발생
                                        Toast.makeText(this@MyPageSettingDetailActivity, "탈퇴 도중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // 재인증 실패
                            Toast.makeText(this@MyPageSettingDetailActivity, "재인증에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun setCameraPermissionCheck() {
        binding.apply {
            layoutUser.setOnClickListener {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!arePermissionsGranted(REQUEST_PERMISSION_TIRAMISU_LIST)) {
                        ActivityCompat.requestPermissions(
                            this@MyPageSettingDetailActivity,
                            REQUEST_PERMISSION_TIRAMISU_LIST,
                            REQUEST_PERMISSION_TIRAMISU
                        )
                    } else {
                        showImageSourceDialog()
                    }
                    return@setOnClickListener
                } else {
                    if (!arePermissionsGranted(REQUEST_PERMISSION_MIN_TIRAMISU_LIST)) {
                        ActivityCompat.requestPermissions(
                            this@MyPageSettingDetailActivity,
                            REQUEST_PERMISSION_MIN_TIRAMISU_LIST,
                            REQUEST_PERMISSION_MIN_TIRAMISU
                        )
                    } else {
                        showImageSourceDialog()
                    }
                }
            }
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    val REQUEST_PERMISSION_TIRAMISU_LIST = arrayOf(
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.CAMERA,
    )

    val REQUEST_PERMISSION_MIN_TIRAMISU_LIST = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private fun showImageSourceDialog() {
        AlertDialog.Builder(this@MyPageSettingDetailActivity)
            .setTitle("Select Image Source")
            .setItems(arrayOf("Camera", "Gallery")) { dialog, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> dispatchPickPictureIntent()
                }
            }
            .show()
    }

    private fun dispatchTakePictureIntent() {
        try {
            val photoFile = createImageFile()
            photoURI = FileProvider.getUriForFile(this@MyPageSettingDetailActivity, "com.th.novelpartymember.provider", photoFile)

            photoURI?.let {
                takePicture.launch(it)
            } ?: run {
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            Toast.makeText(this, "IOException occurred", Toast.LENGTH_SHORT).show()
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun dispatchPickPictureIntent() {
        pickPhoto.launch("image/*")
    }

    private fun loadUserImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val userRef = firestore.collection("users").document(it)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val profileImageUrl = documentSnapshot.getString("profileImageUrl")
                profileImageUrl?.let { url ->
                    Glide.with(this@MyPageSettingDetailActivity)
                        .load(url)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.imgUserPhoto)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "프로필 이미지 로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val fileRef = storageRef.child("userProfileImage/$userId/profile.jpg")
            fileRef.putFile(uri)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveImageUrlToFireStore(downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageUrlToFireStore(downloadUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val userRef = firestore.collection("users").document(userId)
            userRef.update("profileImageUrl", downloadUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
