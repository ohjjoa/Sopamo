package com.th.novelpartymember.view.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.th.novelpartymember.databinding.ItemOnBoardingListBinding
import com.th.novelpartymember.model.OnBoardingItem

class OnBoardingAdapter(
    private val items: List<OnBoardingItem>
) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnBoardingAdapter.OnBoardingAdapterViewHolder {
        val binding = ItemOnBoardingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OnBoardingAdapter.OnBoardingAdapterViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.binding.textTitle.text = item.description
        holder.binding.imageOnBoarding.setImageResource(item.imageResId)
    }

    override fun getItemCount(): Int = items.size

    inner class OnBoardingAdapterViewHolder(val binding: ItemOnBoardingListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}