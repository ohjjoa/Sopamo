package com.th.novelpartymember.custom_view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.th.novelpartymember.R
import com.th.novelpartymember.databinding.LayoutTitleViewBinding
import com.th.novelpartymember.enums.title.TitleMode

class TitleView : ConstraintLayout {
    private lateinit var binding: LayoutTitleViewBinding

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
        setAttribute(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        setAttribute(context, attrs)
    }

    private fun setAttribute(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView)
        val title = typedArray.getString(R.styleable.TitleView_titleText)
        if (!TextUtils.isEmpty(title)) {
            setTitleView(title)
        }
        typedArray.recycle()
    }

    private fun init(context: Context) {
        binding = LayoutTitleViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setTitleView(title: String?) {
        binding.textTitle.text = title
    }

    fun setTitleTextColor(color: Int) {
        binding.textTitle.setTextColor(color)
    }

    fun setMode(titleMode: TitleMode?, context: Context?) {
        when (titleMode) {
            TitleMode.BLACK_BACK -> {
                binding.textTitle.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorBlack
                    )
                )
                binding.imageBack.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_right_back
                    )
                )
                binding.layoutUpdateButton.visibility = GONE
                binding.imageBack.visibility = visibility
            }

            TitleMode.UPDATE_EPISODE -> {
                binding.layoutUpdateButton.visibility = VISIBLE
            }

            else -> {
                binding.textTitle.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorBlack
                    )
                )
                binding.imageBack.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_right_back
                    )
                )
                binding.layoutUpdateButton.visibility = GONE
                binding.imageBack.visibility = GONE
            }
        }
    }

    fun setBackClickListener(onClickListener: OnClickListener?) {
        binding.imageBack.setOnClickListener(onClickListener)
    }

    fun setUpdateButtonClickListener(onClickListener: OnClickListener?) {
        binding.layoutUpdateButton.setOnClickListener(onClickListener)
    }

    fun setUpdateButtonEnable() {
        binding.layoutUpdateButton.isEnabled = true
    }

    fun setUpdateButtonDisable() {
        binding.layoutUpdateButton.isEnabled = false
    }
}

