package com.wat.serviceworkerhelper.view.dashboard.common

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.utils.StepsUtils

open class GuideViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    var titleTV: TextView = itemView.findViewById(R.id.itemTitle)
    var descriptionTV: TextView = itemView.findViewById(R.id.itemDescription)
    private var ratingTV: TextView = itemView.findViewById(R.id.itemRating)

    open fun bind(guide: Guide) {
        titleTV.text = guide.title
        descriptionTV.text = StepsUtils.toString(guide.steps)
        if (guide.opinions.isEmpty()) {
            ratingTV.text = "N/A"
        } else {
            ratingTV.text = String.format("%.1f", guide.rate)
        }
    }
}