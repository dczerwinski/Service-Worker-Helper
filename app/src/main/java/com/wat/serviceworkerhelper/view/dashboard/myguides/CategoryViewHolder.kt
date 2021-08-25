package com.wat.serviceworkerhelper.view.dashboard.myguides

import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.databinding.ItemCategoryBinding
import com.wat.serviceworkerhelper.model.entities.User

class CategoryViewHolder(
    val binding: ItemCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    var isExpanded = false

    fun bind(category: User.Category) {
        binding.categoryName.text = category.name
    }

    fun expand() {
        isExpanded = true
        binding.showMoreIcon.rotation = -90f
//        val r = -90f - showMoreIcon.rotation
//        val d = 300 / (90 / r)
//        showMoreIcon.animate().apply {
//            rotationBy(r)
//            duration = -d.toLong()
//        }.start()
    }

    fun rollUp() {
        isExpanded = false
        binding.showMoreIcon.rotation = 0f
//        val r = -showMoreIcon.rotation
//        val d = 300 / (90 / r)
//        showMoreIcon.animate().apply {
//            rotationBy(r)
//            duration = d.toLong()
//        }.start()
    }
}