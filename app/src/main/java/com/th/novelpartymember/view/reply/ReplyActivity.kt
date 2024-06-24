package com.th.novelpartymember.view.reply

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.th.novelpartymember.MainActivity
import com.th.novelpartymember.UserInfoViewModel
import com.th.novelpartymember.databinding.ActivityReplyBinding
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.model.CommentData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReplyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReplyBinding
    private lateinit var replyAdapter: ReplyAdapter
    private var commentData: CommentData? = null
    private var bookData: BookData? = null
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var userInfoViewModel: UserInfoViewModel

    private var userNickname = ""

    private var userReplyCount = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userInfoViewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        init()
        fetchInitialReplyCount()
        loadReplies()
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResultAndFinish()
        }
    }

    private fun init() {
        this.onBackPressedDispatcher.addCallback(this@ReplyActivity, callback)

        userInfoViewModel.userInfo.observe(this) { userInfo ->
            userNickname = userInfo.nickName
        }

        bookData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("bookData", BookData::class.java)
        } else {
            intent.getParcelableExtra("bookData")
        }

        commentData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("commentData", CommentData::class.java)
        } else {
            intent.getParcelableExtra("commentData")
        }

        replyAdapter = ReplyAdapter()
        binding.apply {
            titleView.setOnClickListener {
                setResultAndFinish()
            }
            textUserNickName.text = commentData?.nickName
            textCommentDate.text = commentData?.date
            textComment.text = commentData?.comment
            textLike.text = "좋아요 ${commentData?.likes}개"

            recyclerReply.apply {
                layoutManager = LinearLayoutManager(this@ReplyActivity)
                adapter = replyAdapter
            }

            // 기존 댓글의 답글 리스트를 어댑터에 설정
            commentData?.replies?.let { replyAdapter.submitList(it) }

            // 답글 추가
            textCommentUpdate.setOnClickListener {
                val replyText = binding.editComment.text.toString()
                if (replyText.isNotEmpty()) {
                    val userId =
                        FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                    val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        Date()
                    )

                    addReply(commentData?.userId.orEmpty(), userId, userNickname, replyText, currentDate)
                }
            }
        }
    }

    private fun fetchInitialReplyCount() {
        val bookId = bookData?.title ?: return
        val bookEpisode = bookData?.episode ?: return
        val commentId = commentData?.userId.orEmpty()

        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode.toString())
            .collection("userComment")
            .document(commentId)
            .collection("replies")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val replyCount = querySnapshot.size() // 초기 답글 개수를 얻어옴
                Log.d(TAG, "Initial number of replies: $replyCount")

                // userReplyCount 초기화
                userReplyCount = replyCount.toString()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching initial replies", e)
            }
    }

    private fun addReply(commentId: String, userId: String, nickName: String, replyText: String, date: String) {
        val bookId = bookData?.title ?: return
        val bookEpisode = bookData?.episode ?: return

        val reply = CommentData(userId = userId, nickName = nickName, comment = replyText, date = date)

        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode.toString())
            .collection("userComment")
            .document(commentId)  // 댓글의 ID
            .collection("replies")
            .add(reply)
            .addOnSuccessListener { documentReference ->
                // 답글을 추가한 후, 해당 댓글의 최신 정보를 다시 가져옴
                db.collection("comments")
                    .document(bookId)
                    .collection("comment")
                    .document(bookEpisode.toString())
                    .collection("userComment")
                    .document(commentId)
                    .collection("replies")
                    .document(documentReference.id) // 새로 추가된 답글의 document ID
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val newReply = documentSnapshot.toObject(CommentData::class.java)

                        // 기존 답글 목록에 추가하지 않도록 중복 확인
                        val existingReplies = commentData?.replies ?: mutableListOf()
                        if (!existingReplies.contains(newReply)) {
                            if (newReply != null) {
                                existingReplies.add(newReply)
                            }
                        }

                        // 최신 답글 목록을 가져와서 로컬 데이터 업데이트
                        commentData?.replies = existingReplies
                        replyAdapter.submitList(existingReplies)
                        binding.editComment.text.clear()
                        scrollToBottom()

                        db.collection("comments")
                            .document(bookId)
                            .collection("comment")
                            .document(bookEpisode.toString())
                            .collection("userComment")
                            .document(commentId)  // 댓글의 ID
                            .collection("replies")
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val replyCount = querySnapshot.size()  // 답글의 개수를 얻음
                                Log.d(TAG, "Number of replies: $replyCount")

                                userReplyCount = replyCount.toString()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error fetching replies", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error fetching updated replies", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding reply", e)
            }
    }

    private fun loadReplies() {
        val bookId = bookData?.title ?: return
        val bookEpisode = bookData?.episode ?: return
        val commentId = commentData?.userId.orEmpty()

        // 기존 댓글의 답글 리스트를 가져옴
        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode.toString())
            .collection("userComment")
            .document(commentId)
            .collection("replies")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val existingReplies = mutableListOf<CommentData>()
                for (document in documents) {
                    val reply = document.toObject(CommentData::class.java)
                    existingReplies.add(reply)
                }
                commentData?.replies = existingReplies
                replyAdapter.submitList(existingReplies)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching existing replies", e)
            }
    }

    private fun scrollToBottom() {
        binding.recyclerReply.post {
            binding.recyclerReply.scrollToPosition(replyAdapter.itemCount - 1)
        }
    }

    private fun setResultAndFinish() {
        val resultIntent = Intent().apply {
            putExtra("userReplyCount", userReplyCount)
            putExtra("commentUserId", commentData?.userId)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
