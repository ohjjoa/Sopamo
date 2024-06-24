package com.th.novelpartymember.view.notify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.th.novelpartymember.databinding.ItemNotifyListBinding
import com.th.novelpartymember.model.NotifyData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotifyAdapter(
    private val notifyItemInterface: NotifyItemInterface
) : RecyclerView.Adapter<NotifyAdapter.NotifyAdapterViewHolder>() {

    private val notifyList = mutableListOf<NotifyData>()

    private val db = Firebase.firestore

    init {
        fetchData()
    }

    private fun fetchData() {
        db.collection("notify").document("userNotify").get().addOnSuccessListener { document ->
            if (document.exists()) {
                val item = document.toObject(NotifyData::class.java)
                if (item != null) {
                    notifyList.clear()
                    notifyList.add(item)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotifyAdapterViewHolder {
        val binding = ItemNotifyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifyAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = notifyList.size

    override fun onBindViewHolder(holder: NotifyAdapterViewHolder, position: Int) {
        val item = notifyList[position]

        holder.binding.apply {
            textTitle.text = item.title
            textDate.text = item.date
        }
    }

    inner class NotifyAdapterViewHolder(val binding: ItemNotifyListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                layoutNotify.setOnClickListener {
                    // 상세로 넘기자
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val notifyData = notifyList[position]
                        notifyItemInterface.notifyItemClickListener(notifyData)
                    }
                }
            }
        }
    }
}
