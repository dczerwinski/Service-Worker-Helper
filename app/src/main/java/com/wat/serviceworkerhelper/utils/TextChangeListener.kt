package com.wat.serviceworkerhelper.utils

import android.text.Editable
import android.text.TextWatcher

interface TextChangeListener : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        /* default implementation */
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        /* default implementation */
    }

    override fun afterTextChanged(s: Editable?) {
        /* default implementation */
    }
}