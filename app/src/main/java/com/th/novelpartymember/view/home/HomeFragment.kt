package com.th.novelpartymember.view.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.th.novelpartymember.UserInfoViewModel
import com.th.novelpartymember.databinding.FragmentHomeBinding
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.view.book_all.BookAllDetailActivity
import com.th.novelpartymember.view.book_detail.BookDetailActivity

class HomeFragment : Fragment(), HomeBookItemListener {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var userInfoViewModel: UserInfoViewModel

    private val binding get() = _binding!!

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var homeWriteBookListAdapter: HomeWriteBookListAdapter

    private lateinit var homeAllBookListAdapter: HomeAllBookListAdapter

    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userInfoViewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]

        init()
        getBookImage()
    }

    private fun init() {
        binding.apply {
            db.collection("books")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val bookCount = querySnapshot.size()
                    textBookTitle.text = "진행중인 소설 ($bookCount)"
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }

            userInfoViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
                textGreeting.text = "소설가 (${userInfo.nickName}) 님의\n작품을 기다리고 있어요!"
            }

            homeWriteBookListAdapter = HomeWriteBookListAdapter(this@HomeFragment)

            recyclerWriteBook.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = homeWriteBookListAdapter
            }

            homeAllBookListAdapter = HomeAllBookListAdapter(this@HomeFragment)

            recyclerAllBook.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = homeAllBookListAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getBookImage() {
        val imagePaths = listOf("test1.png", "test2.png")

        val imagePathMap = mapOf(
            "test2.png" to "홍킬동전",
            "test1.png" to "육국지"
        )

        val userBookData = mutableListOf<BookData>()

        imagePaths.forEach { imagePath ->
            // 해당 이미지 파일 경로에 대응하는 아이템의 title 가져오기
            val itemTitle = imagePathMap[imagePath]

            // 아이템의 title을 사용하여 Firestore에 저장
            itemTitle?.let { title ->
                val storageRef = storage.reference.child(imagePath)

                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        db.collection("books").document(title).update("imageUrl", imageUrl)
                            .addOnSuccessListener {
                                Log.d(TAG, "Image URL for $imagePath saved to Firestore for document $title")

                                db.collection("books").document(title).get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.contains("imageUrl")) {
                                            val imageUrl = document.getString("imageUrl")
                                            imageUrl?.let {
                                                val bookData = document.toObject(BookData::class.java)?.copy(imageUrl = imageUrl)

                                                if (bookData != null) {
                                                    userBookData.add(bookData)
                                                    homeWriteBookListAdapter.setBookList(userBookData)
                                                    homeAllBookListAdapter.setBookList(userBookData)
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "No such document or imageUrl field is missing")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error getting document", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error saving image URL for $imagePath to Firestore", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error getting download URL for $imagePath", e)
                    }
            }
        }
    }

    override fun onReplayClickListener(bookData: BookData) {
        val intent = Intent(requireContext(), BookDetailActivity::class.java)
        intent.putExtra("bookData", bookData)
        startActivity(intent)
    }

    override fun onAllBookClickListener(bookData: BookData) {
        val intent = Intent(requireContext(), BookAllDetailActivity::class.java)
        intent.putExtra("bookData", bookData)
        startActivity(intent)
    }
}
