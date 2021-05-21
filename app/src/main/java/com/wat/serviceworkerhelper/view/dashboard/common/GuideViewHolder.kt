package com.wat.serviceworkerhelper.view.dashboard.common

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wat.serviceworkerhelper.databinding.ItemGuideBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.utils.StepsUtils

open class GuideViewHolder(
    protected val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    open fun bind(guide: Guide) {
        if (binding !is ItemGuideBinding)
            throw IllegalArgumentException("Wrong binding type! binding = $binding")
        binding.itemTitle.text = guide.title
        binding.itemDescription.text = StepsUtils.toString(guide.steps)
        if (guide.opinions.isEmpty()) {
            binding.itemRating.text = "N/A"
        } else {
            binding.itemRating.text = String.format("%.1f", guide.rate)
        }
    }

    fun binding(): ItemGuideBinding = binding as ItemGuideBinding
}