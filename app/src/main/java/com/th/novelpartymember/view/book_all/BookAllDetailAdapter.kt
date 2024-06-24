package com.th.novelpartymember.view.book_all

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.th.novelpartymember.R
import com.th.novelpartymember.databinding.ItemAllBookDetailListBinding
import com.th.novelpartymember.model.AllEpisode

class BookAllDetailAdapter(
    private val bookAllDetailInterface : BookAllDetailInterface
) : RecyclerView.Adapter<BookAllDetailAdapter.BookAllDetailAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookAllDetailAdapter.BookAllDetailAdapterViewHolder {
        val binding = ItemAllBookDetailListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookAllDetailAdapterViewHolder(binding)
    }

    private var allEpisodes: List<AllEpisode> = listOf()

    fun setAllEpisodes(episodes: List<AllEpisode>) {
        val previousSize = allEpisodes.size
        allEpisodes = episodes

        // 이전 크기와 새로운 크기를 비교하여 삽입, 제거 또는 변경된 항목을 알립니다.
        if (episodes.size > previousSize) {
            // 이전에 없었던 새로운 아이템이 추가된 경우
            notifyItemRangeInserted(previousSize, episodes.size - previousSize)
        } else if (episodes.size < previousSize) {
            // 이전에 있었던 아이템이 제거된 경우
            val removedCount = previousSize - episodes.size
            notifyItemRangeRemoved(episodes.size, removedCount)
        } else {
            // 아이템이 변경된 경우
            notifyItemRangeChanged(0, episodes.size)
        }
    }

    override fun onBindViewHolder(
        holder: BookAllDetailAdapter.BookAllDetailAdapterViewHolder,
        position: Int
    ) {
        val episode = allEpisodes[position]
        holder.binding.apply {
            textBookSubTitle.text = episode.episode.toString() + "화 " + episode.subTitle

            textWriter.text = episode.nickName

            val dateText = "등록일 ${episode.createDate}"
            val spannable = SpannableString(dateText)
            val start = dateText.indexOf(episode.createDate)
            val end = start + episode.createDate.length

            val color = ContextCompat.getColor(holder.itemView.context, R.color.colorLightGray)

            // ForegroundColorSpan을 사용하여 텍스트의 색상을 변경
            spannable.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textRegisteredDate.text = spannable

            Glide.with(holder.itemView.context)
                .load(episode.imageUrl)
                .into(imageBook)
        }
    }

    override fun getItemCount(): Int = allEpisodes.size

    inner class BookAllDetailAdapterViewHolder(val binding: ItemAllBookDetailListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                layoutBookContents.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val episode = allEpisodes[position]
                        bookAllDetailInterface.detailItemClickListener(episode)
                    }
                }
            }
        }
    }
}