package com.th.novelpartymember.view.my_write

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.th.novelpartymember.databinding.ItemMyWriteListBinding
import com.th.novelpartymember.model.UserEpisode

class MyWriteAdapter(
    private var userEpisodes: List<UserEpisode>,
    private val myWriteInterface: MyWriteInterface,
) : RecyclerView.Adapter<MyWriteAdapter.MyWriteAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyWriteAdapterViewHolder {
        val binding = ItemMyWriteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyWriteAdapterViewHolder(binding)
    }

    fun updateData(newUserEpisodes: List<UserEpisode>) {
        userEpisodes = newUserEpisodes
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyWriteAdapterViewHolder, position: Int) {
        val userEpisode = userEpisodes[position]
        holder.binding.apply {
            textBookTitle.text = userEpisode.title + userEpisode.episode + "화"
            textSubBookTitle.text = userEpisode.subTitle
            textRegisteredDate.text = "등록일 ${userEpisode.createDate}"
            textWriter.text = userEpisode.nickName

            Glide.with(root.context)
                .load(userEpisode.imageUrl)
                .into(imageBook)
        }
    }

    override fun getItemCount(): Int = userEpisodes.size

    inner class MyWriteAdapterViewHolder(
        val binding: ItemMyWriteListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 다이얼로그 띄우기
             binding.apply {
                 root.setOnClickListener {
                     val position = adapterPosition
                     if (position != RecyclerView.NO_POSITION) {
                         val userEpisode = userEpisodes[position]
                         myWriteInterface.onContentBookDetailListener(userEpisode)
                     }
                 }
             }
        }
    }
}