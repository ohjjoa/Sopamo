package com.th.novelpartymember.view.reply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.th.novelpartymember.databinding.ItemReplyListBinding
import com.th.novelpartymember.model.CommentData

class ReplyAdapter : ListAdapter<CommentData, ReplyAdapter.ReplyAdapterViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyAdapterViewHolder {
        val binding = ItemReplyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReplyAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyAdapterViewHolder, position: Int) {
        val reply = getItem(position)
        holder.bind(reply)
    }

    inner class ReplyAdapterViewHolder(private val binding: ItemReplyListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: CommentData) {
            binding.apply {
                textUserNickName.text = reply.nickName
                textCommentDate.text = reply.date
                textComment.text = reply.comment
                textLike.text = "좋아요 ${reply.likes}개"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CommentData>() {
        override fun areItemsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
            return oldItem.userId == newItem.userId && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
            return oldItem == newItem
        }
    }
}
