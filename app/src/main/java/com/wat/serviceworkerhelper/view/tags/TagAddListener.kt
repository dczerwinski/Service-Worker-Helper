package com.wat.serviceworkerhelper.view.tags

import android.widget.EditText
import com.wat.serviceworkerhelper.utils.TextChangeListener

class TagAddListener(
    private val tagsAdapter: TagsRecyclerViewAdapter,
    private val editText: EditText,
    private val onTagAddListener: OnTagAddListener
) : TextChangeListener {

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null && s.isNotEmpty() && s.last() == ',') {
            tagsAdapter.addToList(s.substring(0, s.length - 1))
            onTagAddListener.onTagAdd()
            editText.setText("")
        }
    }
}