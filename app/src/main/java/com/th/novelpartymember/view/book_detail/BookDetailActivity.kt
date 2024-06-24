package com.th.novelpartymember.view.book_detail

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.th.novelpartymember.BookInfoViewModel
import com.th.novelpartymember.R
import com.th.novelpartymember.databinding.ActivityBookDetailBinding
import com.th.novelpartymember.enums.title.TitleMode
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.model.EpisodeLikes
import com.th.novelpartymember.view.dialog.CommentBottomDialog
import com.th.novelpartymember.view.nextEpisode.NextEpisodeActivity

class BookDetailActivity : AppCompatActivity(), CommentBottomDialog.CommentListener {

    private lateinit var binding: ActivityBookDetailBinding

    private val commentBottomDialog by lazy {
        CommentBottomDialog()
    }

    private val db = Firebase.firestore

    private var bookData: BookData? = null

    private val viewModel: BookInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        observeViewModel()

        fetchComments()

    }

    private fun init() {
        bookData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("bookData", BookData::class.java)
        } else {
            intent.getParcelableExtra("bookData")
        }

        binding.apply {
            titleView.setTitleView("${bookData?.title} ${bookData?.episode}${"화"}")

            titleView.setOnClickListener {
                finish()
            }

            titleView.setMode(TitleMode.BLACK_BACK, this@BookDetailActivity)

            textContent.text = bookData?.content

            layoutNextContent.setOnClickListener {
                val intent = Intent(this@BookDetailActivity, NextEpisodeActivity::class.java)
                intent.putExtra("bookData", bookData)
                startActivity(intent)
            }

            layoutLike.setOnClickListener {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val isLiked = viewModel.episodeLikes.value?.episodeLikedBy?.contains(userId) == true

                    val likes = viewModel.episodeLikes.value?.episodeLikes ?: 0

                    if (isLiked) {
                        viewModel.episodeLikes.value?.episodeLikedBy?.remove(userId)
                        viewModel.episodeLikes.value?.episodeLikes = likes - 1
                        imageLike.setBackgroundResource(R.drawable.ic_like)
                    } else {
                        viewModel.episodeLikes.value?.episodeLikedBy?.add(userId)
                        viewModel.episodeLikes.value?.episodeLikes = likes + 1
                        imageLike.setBackgroundResource(R.drawable.ic_like_click)
                    }

                    db.collection("books")
                        .document(bookData?.title.toString())
                        .collection("allEpisodes")
                        .document(bookData?.episode.toString())
                        .collection("episodeLikes")
                        .document(userId)
                        .set(viewModel.episodeLikes.value!!) // 좋아요 정보를 Firestore에 업데이트
                        .addOnSuccessListener {
                            // 업데이트 성공 시 작업
                            binding.textLikeNumber.text = viewModel.episodeLikes.value?.episodeLikes.toString()
                            Log.d(TAG, "Like status updated successfully")
                        }
                        .addOnFailureListener { e ->
                            // 업데이트 실패 시 작업
                            Log.w(TAG, "Error updating like status", e)
                            // 실패한 경우 좋아요 상태 롤백 등의 작업 수행 가능
                        }
                }
            }

            layoutLikeComment.setOnClickListener {
                commentBottomDialog.run {
                    val bundle = Bundle()
                    bundle.putParcelable("bookData", bookData)

                    val dialog = CommentBottomDialog()
                    dialog.arguments = bundle
                    dialog.show(supportFragmentManager, "comment_dialog")
                }
            }
        }
        fetchLikes()
    }

    private fun fetchLikes() {
        db.collection("books")
            .document(bookData?.title.toString())
            .collection("allEpisodes")
            .document(bookData?.episode.toString())
            .collection("episodeLikes")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        val likes = document.getLong("episodeLikes")?.toInt() ?: 0
                        val likedBy = document.get("episodeLikedBy") as? MutableList<String> ?: mutableListOf()

                        val episodeLikes = EpisodeLikes(likes, likedBy)
                        viewModel.setEpisodeLikes(episodeLikes)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun observeViewModel() {
        viewModel.episodeLikes.observe(this@BookDetailActivity) { episodeLikes ->
            binding.textLikeNumber.text = episodeLikes.episodeLikes.toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (episodeLikes.episodeLikedBy.contains(userId)) {
                binding.imageLike.setBackgroundResource(R.drawable.ic_like_click)
            } else {
                binding.imageLike.setBackgroundResource(R.drawable.ic_like)
            }
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
                val count = documents.size()
                binding.textCommentNumber.text = count.toString()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    override fun onCommentAdded() {
        fetchComments()
    }
}
