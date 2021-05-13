package com.wat.serviceworkerhelper.view.dashboard.myguides

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.User

class CategoryViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private var categoryName: TextView = itemView.findViewById(R.id.categoryName)
    var isExpanded = true
    var showMoreIcon: ImageView = itemView.findViewById(R.id.showMoreIcon)

    init {
        showMoreIcon.rotation = -90f
    }

    fun bind(category: User.Category) {
        categoryName.text = category.name
    }

    fun expand() {
        isExpanded = true
        showMoreIcon.rotation = -90f
//        val r = -90f - showMoreIcon.rotation
//        val d = 300 / (90 / r)
//        showMoreIcon.animate().apply {
//            rotationBy(r)
//            duration = -d.toLong()
//        }.start()
    }

    fun rollUp() {
        isExpanded = false
        showMoreIcon.rotation = 0f
//        val r = -showMoreIcon.rotation
//        val d = 300 / (90 / r)
//        showMoreIcon.animate().apply {
//            rotationBy(r)
//            duration = d.toLong()
//        }.start()
    }
}