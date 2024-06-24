package com.th.novelpartymember.view.book_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.th.novelpartymember.R
import com.th.novelpartymember.databinding.ItemCommentBinding
import com.th.novelpartymember.model.CommentData
import com.google.firebase.auth.FirebaseAuth

class BookDetailCommentAdapter(
    private val bookDetailInterface: BookDetailInterface
) : RecyclerView.Adapter<BookDetailCommentAdapter.BookDetailCommentAdapterViewHolder>() {

    var commentList = mutableListOf<CommentData>()

    fun notifyDataChanged() {
        notifyDataSetChanged()
    }

    fun setReplyCount(userId: String, replyCount: Int) {
        val comment = commentList.find { it.userId == userId }
        comment?.replyCount = replyCount

        // UI를 업데이트합니다.
        notifyItemChanged(commentList.indexOf(comment))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookDetailCommentAdapterViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookDetailCommentAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookDetailCommentAdapterViewHolder, position: Int) {
        val item = commentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = commentList.size

    fun addComment(commentData: CommentData) {
        commentList.add(commentData)
        notifyItemInserted(commentList.size - 1)
    }

    inner class BookDetailCommentAdapterViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                imgHart.setOnClickListener {
                    val item = commentList[adapterPosition]
                    bookDetailInterface.onLikeButtonClickListener(item)
                }

                layoutSpeech.setOnClickListener {
                    val item = commentList[adapterPosition]
                    bookDetailInterface.moveReplyListener(item)
                }
            }
        }

        fun bind(item: CommentData) {
            binding.apply {
                textUserId.text = item.nickName
                textComment.text = item.comment
                textCommentTime.text = item.date
                textLikeNumber.text = item.likes.toString()

                textReplyCount.text = item.replyCount.toString()
            }
            updateLikeImage(item)
        }

        private fun updateLikeImage(item: CommentData) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val isLiked = item.likedBy.contains(userId)

            if (isLiked) {
                binding.imgHart.setImageResource(R.drawable.ic_like_click)
            } else {
                binding.imgHart.setImageResource(R.drawable.ic_like)
            }
        }
    }
}
