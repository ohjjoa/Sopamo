package com.th.novelpartymember.view.dialog

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.th.novelpartymember.R
import com.th.novelpartymember.databinding.DialogBottomCommentBinding
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.model.CommentData
import com.th.novelpartymember.view.book_detail.BookDetailCommentAdapter
import com.th.novelpartymember.view.book_detail.BookDetailInterface
import com.th.novelpartymember.view.reply.ReplyActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentBottomDialog : BottomSheetDialogFragment(), BookDetailInterface{

    private lateinit var binding: DialogBottomCommentBinding

    private var bookData: BookData? = null

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val db = Firebase.firestore

    private lateinit var bookDetailCommentAdapter: BookDetailCommentAdapter

    private var commentListener: CommentListener? = null

    private lateinit var replyChangeLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogBottomCommentBinding.inflate(inflater, container, false)

        replyChangeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val commentUserId = data?.getStringExtra("commentUserId") ?: ""
                val replyCountString = data?.getStringExtra("userReplyCount") ?: "0"
                val replyCount = replyCountString.toIntOrNull() ?: 0 // Convert String to Int, default to 0 if conversion fails

                bookDetailCommentAdapter.setReplyCount(commentUserId, replyCount)
            } else {
                Log.d("CommentBottomDialog", "Activity result canceled or not OK")
            }
        }

        init()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CommentListener) {
            commentListener = context
        } else {
            throw RuntimeException("$context must implement CommentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        commentListener = null
    }

    private fun init() {
        binding.apply {
            imgClose.setOnClickListener {
                dismiss()
            }
            bookDetailCommentAdapter = BookDetailCommentAdapter(this@CommentBottomDialog)

            recyclerComment.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = bookDetailCommentAdapter
            }

            textCommentUpdate.setOnClickListener {
                val newComment = editComment.text.toString()

                if (newComment.isNotEmpty()) {
                    addCommentToFireStore(newComment)

                    recyclerComment.scrollToPosition(bookDetailCommentAdapter.itemCount - 1)

                    commentListener?.onCommentAdded()

                    val imm = requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)

                    editComment.setText("")

                    return@setOnClickListener
                }
                Toast.makeText(requireContext(), "댓글을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        val arguments = arguments
        bookData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("bookData", BookData::class.java)
        } else {
            arguments?.getParcelable("bookData")
        }

        fetchComments()
    }

    private fun addCommentToFireStore(comment: String) {
        val bookId = bookData?.title ?: return
        val bookEpisode = bookData?.episode?: return

        // Get the current date
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()).toString()

        // Get the current user's UID
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Firestore에서 사용자의 닉네임을 가져오기
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nickName = document.getString("nickName") ?: ""

                        // Create a CommentData object with comment, nickName, userId, likes, likedBy, and date
                        val commentData = CommentData(
                            nickName = nickName,
                            userId = userId,
                            comment = comment,
                            likes = 0, // 초기 좋아요 수는 0
                            likedBy = mutableListOf(), // 초기 좋아요를 누른 사용자 목록은 비어 있음
                            date = currentDate
                        )

                        // Add the CommentData object to Firestore under the specific book's comments collection
                        db.collection("comments")
                            .document(bookId)
                            .collection("comment")
                            .document(bookEpisode.toString())
                            .collection("userComment")
                            .add(commentData)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    ContentValues.TAG,
                                    "DocumentSnapshot written with ID: ${documentReference.id}"
                                )
                                bookDetailCommentAdapter.addComment(commentData)
                                binding.recyclerComment.scrollToPosition(bookDetailCommentAdapter.itemCount - 1)
                                updateCommentVisibility()
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error writing document", e)
                                Toast.makeText(
                                    requireContext(),
                                    "Error adding comment.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ${exception.message}")
                    Toast.makeText(
                        requireContext(),
                        "Error getting documents: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun updateCommentVisibility() {
        val hasComments = bookDetailCommentAdapter.itemCount > 0
        binding.apply {
            textNotComment.visibility = if (hasComments) View.GONE else View.VISIBLE
            recyclerComment.visibility = if (hasComments) View.VISIBLE else View.GONE
        }
    }

    private fun fetchComments() {
        val bookId = bookData?.title ?: return
        val bookEpisode = bookData?.episode?: return

        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode.toString())
            .collection("userComment")
            .get()
            .addOnSuccessListener { documents ->
                val hasComments = !documents.isEmpty
                binding.textNotComment.visibility = if (hasComments) View.GONE else View.VISIBLE
                binding.recyclerComment.visibility = if (hasComments) View.VISIBLE else View.GONE

                for (document in documents) {
                    if (document.exists()) {
                        val item = document.toObject(CommentData::class.java).copy(userId = document.id)
                        bookDetailCommentAdapter.addComment(item)

                        fetchReplies(bookId, bookEpisode.toString(), document.id)
                    }
                }
                binding.recyclerComment.scrollToPosition(bookDetailCommentAdapter.itemCount - 1)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun fetchReplies(bookId: String, bookEpisode: String, commentId: String) {
        db.collection("comments")
            .document(bookId)
            .collection("comment")
            .document(bookEpisode)
            .collection("userComment")
            .document(commentId)
            .collection("replies")
            .get()
            .addOnSuccessListener { documents ->
                val replyCount = documents.size()
                bookDetailCommentAdapter.setReplyCount(commentId, replyCount)
            }
            .addOnFailureListener { exception ->
                Log.w("CommentBottomDialog", "Error getting replies.", exception)
            }
    }

    fun dismissDialog() {
        dismiss()
    }

    override fun onLikeButtonClickListener(comment: CommentData) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val commentRef =
            db.collection("comments")
              .document(bookData?.title.toString())
              .collection("comment")
              .document(bookData?.episode.toString())
              .collection("userComment")
              .document(comment.userId)


        db.runTransaction { transaction ->
            val snapshot = transaction.get(commentRef)
            val likedBy = snapshot.get("likedBy") as MutableList<String> // 리스트 타입 변경
            var likes = (snapshot.get("likes") as Long).toInt()

            if (likedBy.contains(userId)) {
                // 좋아요 취소
                transaction.update(commentRef, "likes", FieldValue.increment(-1))
                likedBy.remove(userId) // 사용자 ID 제거
                transaction.update(commentRef, "likedBy", likedBy) // 업데이트된 likedBy 리스트로 업데이트
                likes--
            } else {
                // 좋아요 추가
                transaction.update(commentRef, "likes", FieldValue.increment(1))
                likedBy.add(userId) // 사용자 ID 추가
                transaction.update(commentRef, "likedBy", likedBy) // 업데이트된 likedBy 리스트로 업데이트
                likes++
            }

            // 업데이트된 좋아요 수를 반환합니다.
            Pair(likes, likedBy) // Pair를 반환하여 좋아요 수와 업데이트된 likedBy 리스트를 함께 반환합니다.
        }.addOnSuccessListener { (newLikes, updatedLikedBy) ->
            // 업데이트된 좋아요 수를 댓글 데이터에 반영합니다.
            comment.likes = newLikes
            comment.likedBy = updatedLikedBy // 업데이트된 likedBy 리스트를 CommentData 객체에 반영합니다.
            // 어댑터에 변경 사항을 알립니다.
            bookDetailCommentAdapter.notifyDataChanged()

            Log.d(ContentValues.TAG, "Transaction success!")
        }.addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Transaction failure.", e)
        }
    }

    override fun moveReplyListener(comment: CommentData) {
        val intent = Intent(requireContext(), ReplyActivity::class.java).apply {
            putExtra("commentData", comment)
            putExtra("bookData", bookData)
        }
        replyChangeLauncher.launch(intent)
    }

    interface CommentListener {
        fun onCommentAdded()
    }
}
