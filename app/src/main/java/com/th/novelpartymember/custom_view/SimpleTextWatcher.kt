package com.th.novelpartymember.custom_view

import android.text.Editable
import android.text.TextWatcher

class SimpleTextWatcher(val simpleTextChanged: (CharSequence?, Int, Int, Int) -> Unit) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        simpleTextChanged(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {
    }
}
