package com.th.novelpartymember.view.home

import android.content.ContentValues
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.th.novelpartymember.databinding.ItemAllBookListBinding
import com.th.novelpartymember.model.BookData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.th.novelpartymember.R

class HomeAllBookListAdapter(
    private val homeBookItemListener: HomeBookItemListener
) : RecyclerView.Adapter<HomeAllBookListAdapter.AllBookListAdapterViewHolder>() {

    private val bookList = mutableListOf<BookData>()

    fun setBookList(newBookList: List<BookData>) {
        bookList.clear()
        bookList.addAll(newBookList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllBookListAdapterViewHolder {
        val binding = ItemAllBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllBookListAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AllBookListAdapterViewHolder,
        position: Int
    ) {
        val item = bookList[position]
        holder.binding.apply {
            textBookTitle.text = item.title

            textWriter.text = item.nickName

            val dateText = "등록일 ${item.createDate}"
            val spannable = SpannableString(dateText)
            val start = dateText.indexOf(item.createDate)
            val end = start + item.createDate.length

            val color = ContextCompat.getColor(holder.itemView.context, R.color.colorLightGray)

            spannable.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textRegisteredDate.text = spannable

            textInProgress.text = item.episode.toString() + "화 진행중"

            Glide.with(root.context)
                .load(item.imageUrl)
                .into(imageBook)
        }
    }

    override fun getItemCount(): Int = bookList.size

    inner class AllBookListAdapterViewHolder(val binding: ItemAllBookListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    val bookData = bookList[position]
                    homeBookItemListener.onAllBookClickListener(bookData)
                }
            }
        }
    }
}