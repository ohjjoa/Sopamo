package com.th.novelpartymember.view.book_all_content

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.th.novelpartymember.BookInfoViewModel
import com.th.novelpartymember.databinding.ActivityBookAllContentBinding
import com.th.novelpartymember.enums.title.TitleMode
import com.th.novelpartymember.model.AllEpisode
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.model.CommentData
import com.th.novelpartymember.model.EpisodeLikes

class BookAllContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookAllContentBinding

    private var episode: AllEpisode? = AllEpisode()

    private var bookData: BookData? = BookData()

    private val db = Firebase.firestore

    private lateinit var viewModel: BookInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBookAllContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this)[BookInfoViewModel::class.java]

        episode = intent.getParcelableExtra("episode")

        bookData = intent.getParcelableExtra("bookData")

        binding.apply {
            titleView.setOnClickListener {
                finish()
            }

            titleView.setMode(TitleMode.BLACK_BACK, this@BookAllContentActivity)

            // 에피소드가 null이 아니면 해당 에피소드의 제목을 표시합니다.
            episode?.let {
                titleView.setTitleView(it.subTitle)
                textContent.text = it.content
            }
        }
        fetchLikes()
        fetchComments()
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
                        binding.textLikeNumber.text = likes.toString()
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
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
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }
}
