package com.wat.serviceworkerhelper.view.tags

import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.databinding.ItemTagBinding

class TagViewHolder(
    private val binding: ItemTagBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setUp(tagContent: String, isEditable: Boolean) {
        binding.tagContent.text = tagContent
    }

    fun xIcon() = binding.xIcon
}