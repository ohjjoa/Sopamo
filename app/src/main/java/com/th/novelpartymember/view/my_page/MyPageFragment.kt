package com.th.novelpartymember.view.my_page

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.th.novelpartymember.UserInfoViewModel
import com.th.novelpartymember.databinding.FragmentMyPageBinding
import com.th.novelpartymember.view.dialog.DeveloperInfoDialog
import com.th.novelpartymember.view.dialog.LogoutDialog
import com.th.novelpartymember.view.notify.NotifyActivity
import com.th.novelpartymember.view.onboarding.OnBoardingActivity
import java.text.SimpleDateFormat
import java.util.Locale

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null

    private val binding get() = _binding!!

    private lateinit var userInfoViewModel: UserInfoViewModel

    private val userLogOutDialog: LogoutDialog by lazy {
        LogoutDialog(requireContext())
    }

    private lateinit var storageRef: StorageReference

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userInfoViewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]

        init()
    }

    private fun init() {
        storageRef = FirebaseStorage.getInstance().reference

        firestore = FirebaseFirestore.getInstance()

        binding.apply {

            val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())

            userInfoViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
                textUserNickName.text = userInfo.nickName
                textUserEmail.text = userInfo.email
                userInfo.createdAt?.let {
                    val dateText = dateFormat.format(it)
                    val signUpDateText = "작가 등록일 $dateText"
                    val spannableString = SpannableString(signUpDateText)
                    val blackColorSpan = ForegroundColorSpan(Color.BLACK)

                    // Apply black color to "작가 등록일"
                    spannableString.setSpan(
                        blackColorSpan,
                        0,
                        6,  // Length of "작가 등록일"
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    textSignUpDate.text = spannableString
                }
            }

            layoutUserManager.setOnClickListener {
                userInfoViewModel.userInfo.value.let { userInfo ->
                    val intent = Intent(activity, MyPageSettingDetailActivity::class.java)
                    intent.putExtra("userInfo", userInfo)
                    startActivity(intent)
                }
            }

            layoutQuestion.setOnClickListener {
                val dialog = DeveloperInfoDialog(requireContext(), null)
                dialog.show()
            }

            layoutLicense.setOnClickListener {
                startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            }

            layoutNotify.setOnClickListener {
                startActivity(Intent(activity, NotifyActivity::class.java))
            }

            layoutLogOut.setOnClickListener {
                userLogOutDialog.setMessage("로그아웃하시겠습니까?")
                    .setOnConfirmClick { logout() }
                    .setOnCancelClick { userLogOutDialog.dismiss() }
                    .show()
            }
        }
        loadUserImage()
    }

    private fun logout() {
        val auth = getInstance()
        auth.signOut()
        startActivity(
            Intent(activity, OnBoardingActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUserImage() {
        val userId = getInstance().currentUser?.uid
        userId?.let {
            val userRef = firestore.collection("users").document(it)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val profileImageUrl = documentSnapshot.getString("profileImageUrl")
                profileImageUrl?.let { url ->
                    Glide.with(this)
                        .load(url)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.imgUserPhoto)
                }
            }.addOnFailureListener {
//                Toast.makeText(this, "프로필 이미지 로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
