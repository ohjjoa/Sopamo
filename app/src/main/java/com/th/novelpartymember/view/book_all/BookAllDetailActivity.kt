package com.th.novelpartymember.view.book_all

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.th.novelpartymember.databinding.ActivityBookAllDetailBinding
import com.th.novelpartymember.model.AllEpisode
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.view.book_all_content.BookAllContentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.th.novelpartymember.R
import com.th.novelpartymember.enums.title.TitleMode

class BookAllDetailActivity : AppCompatActivity(), BookAllDetailInterface {
    private lateinit var binding: ActivityBookAllDetailBinding

    private var bookData: BookData? = null

    private lateinit var bookAllDetailAdapter: BookAllDetailAdapter

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var allEpisodes: MutableList<AllEpisode> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBookAllDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        bookData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("bookData", BookData::class.java)
        } else {
            intent.getParcelableExtra("bookData")
        }

        binding.apply {
            titleView.setOnClickListener {
                finish()
            }
            titleView.setMode(TitleMode.BLACK_BACK, this@BookAllDetailActivity)

            textBookTitle.text = bookData?.title
            textWriter.text = bookData?.nickName

            Glide.with(this@BookAllDetailActivity)
                .load(bookData?.imageUrl)
                .into(imageBook)

            val dateText = "등록일 ${bookData?.createDate}"
            val spannable = SpannableString(dateText)

            val start = dateText.indexOf(bookData?.createDate.toString())
            val end = start + (bookData?.createDate?.length ?: 0)

            val color = ContextCompat.getColor(this@BookAllDetailActivity, R.color.colorLightGray)

            spannable.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textRegisteredDate.text = spannable

            layoutBookContents.visibility = View.VISIBLE

            textContent.text = bookData?.content

            bookAllDetailAdapter = BookAllDetailAdapter(this@BookAllDetailActivity)

            recyclerBookDetail.apply {
                layoutManager = LinearLayoutManager(this@BookAllDetailActivity)
                adapter = bookAllDetailAdapter
            }
        }
        fetchEpisodesFromFireStore()
    }

    private fun fetchEpisodesFromFireStore() {
        bookData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("bookData", BookData::class.java)
        } else {
            intent.getParcelableExtra("bookData")
        }

        val bookId = bookData?.title ?: return

        db.collection("books")
            .document(bookId)
            .collection("allEpisodes")
            .get()
            .addOnSuccessListener { episodeDocuments ->
                val hasContent = !episodeDocuments.isEmpty
                binding.textNotContent.visibility = if (hasContent) View.GONE else View.VISIBLE
                binding.recyclerBookDetail.visibility = if (hasContent) View.VISIBLE else View.GONE

                for (episodeDocument in episodeDocuments) {
                    val title = episodeDocument.getString("title") ?: ""
                    val subTitle = episodeDocument.getString("subTitle") ?: ""
                    val episode = episodeDocument.getLong("episode") ?: 0
                    val content = episodeDocument.getString("content") ?: ""
                    val createDate = episodeDocument.getString("createDate") ?: ""
                    val nickName = episodeDocument.getString("nickName") ?: ""
                    this@BookAllDetailActivity.allEpisodes.add(
                        AllEpisode(
                            content,
                            episode,
                            subTitle,
                            title,
                            createDate,
                            nickName,
                            imageUrl = bookData?.imageUrl.toString()
                        )
                    )
                }
                bookAllDetailAdapter.setAllEpisodes(allEpisodes.toList())
            }
            .addOnFailureListener { e ->
                print(e)
            }
    }

    override fun detailItemClickListener(episode: AllEpisode) {
        val intent = Intent(this, BookAllContentActivity::class.java)
        intent.putExtra("episode", episode)
        intent.putExtra("bookData", bookData)
        startActivity(intent)
    }
}