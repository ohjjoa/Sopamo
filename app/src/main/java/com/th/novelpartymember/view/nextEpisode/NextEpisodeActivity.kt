package com.th.novelpartymember.view.nextEpisode

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.th.novelpartymember.MainActivity
import com.th.novelpartymember.custom_view.SimpleTextWatcher
import com.th.novelpartymember.databinding.ActivityNextEpisodeBinding
import com.th.novelpartymember.model.BookData
import com.th.novelpartymember.model.UserEpisode
import com.th.novelpartymember.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.th.novelpartymember.enums.title.TitleMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NextEpisodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNextEpisodeBinding

    private val db = Firebase.firestore

    private var bookData: BookData? = null

    private val currentEpisode = bookData?.episode ?: 1L

    private val nextEpisode = currentEpisode + 1

    private val nextEpisodeString = " ${nextEpisode}화"

    private val userEpisodes = mutableListOf<UserEpisode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNextEpisodeBinding.inflate(layoutInflater)
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
            val subTitle = editSubTitle.text

            val content = editContent.text

            titleView.setOnClickListener {
                finish()
            }

            titleView.setMode(TitleMode.UPDATE_EPISODE, this@NextEpisodeActivity)

            titleView.setUpdateButtonClickListener {
                if (subTitle.isEmpty() && content.isEmpty()) {
                    ToastUtils.showToast(this@NextEpisodeActivity, "타을틀, 소설을 입력해주세요.")
                } else {
                    addUserEpisode()
                }
            }

            textWriteInfo.text = bookData?.title + nextEpisodeString

            editSubTitle.apply {
                addTextChangedListener(SimpleTextWatcher { text, _, _, _ ->
                    val novelTitle = text.toString()
                    val novelContent = editContent.text.toString()

                    if (novelTitle.isEmpty() && novelContent.isEmpty()) {
                        titleView.setUpdateButtonDisable()
                    } else {
                        titleView.setUpdateButtonEnable()
                    }
                })
            }

            editContent.apply {
                addTextChangedListener(SimpleTextWatcher { text, _, _, _ ->
                    val novelTitle = text.toString()
                    val novelContent = editContent.text.toString()
                    if (novelContent.isEmpty() && novelTitle.isEmpty()) {
                        titleView.setUpdateButtonDisable()
                    } else {
                        titleView.setUpdateButtonEnable()
                    }
                })
            }

            layoutDeleteTitle.setOnClickListener {
                editSubTitle.setText("")
            }

            layoutDeleteNovel.setOnClickListener {
                editContent.setText("")
            }

            layoutCopyNovel.setOnClickListener {
                val textToCopy = editContent.text.toString()
                if (textToCopy.isNotEmpty()) {
                    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("NovelContent", textToCopy)
                    clipboardManager.setPrimaryClip(clipData)
                    ToastUtils.showToast(this@NextEpisodeActivity, "소설 내용이 복사되었습니다.")
                } else {
                    ToastUtils.showToast(this@NextEpisodeActivity, "복사할 내용이 없습니다.")
                }
            }
        }
    }

    private fun addUserEpisode() {
        val bookId = bookData?.title ?: return

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val content = binding.editContent.text.toString()

        val subContent = binding.editSubTitle.text.toString()

        val currentDate =
            SimpleDateFormat("yy.MM.dd", Locale.getDefault()).format(Date()).toString()

        val userEpisodeContent = UserEpisode(content = content, createDate = currentDate,
            subTitle = subContent, episode = nextEpisode, imageUrl = bookData?.imageUrl ?: "")

        val userEpisodeCollection = db.collection("books")
            .document(bookId)
            .collection("userEpisode")

        userEpisodeCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val documentId = nextEpisode.toString()

                // Add the CommentData object to Firestore under the specific document ID and userId
                userEpisodeCollection.document(documentId)
                    .collection(userId) // Using a sub-collection for the user ID
                    .add(userEpisodeContent) // Add the comment data under the user ID sub-collection
                    .addOnSuccessListener {
                        userEpisodes.add(userEpisodeContent)
                        val intent = Intent(this@NextEpisodeActivity, MainActivity::class.java).apply {
                            putExtra("RETURN_TO_SECOND_PAGE", true)
                            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        ToastUtils.showToast(this@NextEpisodeActivity, "다음화 쓰기가 완료되었습니다. 다음화에 최신화에 글이 올라갑니다.")
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error writing document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents count", e)
            }
    }
}