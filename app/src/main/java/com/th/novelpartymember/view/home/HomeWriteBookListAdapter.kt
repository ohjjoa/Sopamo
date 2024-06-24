package com.th.novelpartymember.view.home

import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.th.novelpartymember.R
import com.th.novelpartymember.databinding.ItmeWriteBookListBinding
import com.th.novelpartymember.model.BookData

class HomeWriteBookListAdapter(
    private val homeBookItemListener: HomeBookItemListener
) : RecyclerView.Adapter<HomeWriteBookListAdapter.WriteBookListAdapterViewHolder>() {

    private val bookList = mutableListOf<BookData>()

    fun setBookList(newBookList: List<BookData>) {
        bookList.clear()
        bookList.addAll(newBookList)
        notifyDataSetChanged()
    }

    // 아이템의 위치를 저장
    private var currentItemPosition = 0

    // companion object를 사용하여 일정 시간을 상수로 정의
    companion object {
        private const val TIME_INTERVAL = 5000L // 5초마다 다음 아이템으로 이동
    }

    // 다음 아이템으로 이동
    private val handler = Handler(Looper.getMainLooper())

    private val scrollRunnable = object : Runnable {
        override fun run() {
            // 다음 아이템으로 스크롤
            currentItemPosition++
            if (currentItemPosition >= itemCount) {
                currentItemPosition = 0 // 리스트의 처음으로 이동
            }
            // 다음 아이템으로 스크롤 smoothScrollToPosition 메서드 호출
            (recyclerView?.layoutManager as? LinearLayoutManager)?.smoothScrollToPosition(
                recyclerView,
                RecyclerView.State(),
                currentItemPosition
            )
            handler.postDelayed(this, TIME_INTERVAL) // 일정 시간마다 반복
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WriteBookListAdapterViewHolder {
        val binding =
            ItmeWriteBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WriteBookListAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = bookList.size

    override fun onBindViewHolder(holder: WriteBookListAdapterViewHolder, position: Int) {
        val item = bookList[position]
        holder.binding.apply {
            textBookTitle.text = item.title

//            imageGif.visibility = View.GONE

            textBookTitle.text = item.title + item.episode + "화"
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

            layoutBookContents.visibility = View.VISIBLE

            Glide.with(root.context)
                .load(item.imageUrl)
                .into(imageBook)
        }
    }

    inner class WriteBookListAdapterViewHolder(val binding: ItmeWriteBookListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.layoutBookContents.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val bookData = bookList[position]
                    homeBookItemListener.onReplayClickListener(bookData)
                }
            }
        }
    }

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        startAutoScroll()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
        stopAutoScroll()
    }

    private fun startAutoScroll() {
        handler.postDelayed(scrollRunnable, TIME_INTERVAL)
    }

    private fun stopAutoScroll() {
        handler.removeCallbacks(scrollRunnable)
    }
}
