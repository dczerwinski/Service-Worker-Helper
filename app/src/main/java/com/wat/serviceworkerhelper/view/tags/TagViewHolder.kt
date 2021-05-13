package com.wat.serviceworkerhelper.view.tags

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R

class TagViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val tagContent: TextView = itemView.findViewById(R.id.tagContent)
    val xIcon: ImageView = itemView.findViewById(R.id.xIcon)

    fun setUp(tagContent: String, isEditable: Boolean) {
        this.tagContent.text = tagContent
    }
}