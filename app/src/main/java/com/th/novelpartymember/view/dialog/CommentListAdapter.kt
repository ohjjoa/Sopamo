package com.th.novelpartymember.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.th.novelpartymember.databinding.ItemCommentListBinding

class CommentListAdapter(
    context: Context,
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = 4

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
//        val certificateItem = certificateItem[position]

        if (convertView == null) {
            val binding = ItemCommentListBinding.inflate(inflater)
            convertView = binding.root
            convertView.tag = binding


            binding.apply {
                binding.textComment.text = "하위"
            }
        } else {
            val binding = convertView.tag as ItemCommentListBinding

            binding.apply {
                binding.textComment.text = "하위"
            }
        }
        return convertView
    }
}
