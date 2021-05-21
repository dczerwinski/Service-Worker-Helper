package com.wat.serviceworkerhelper.view.dashboard.pendingnewguides

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wat.serviceworkerhelper.databinding.ItemGuideBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.utils.MyRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.dashboard.common.GuideViewHolder

class PendingNewGuidesRecyclerViewAdapter(
    private val activity: Activity
) : MyRecyclerViewAdapter<GuideViewHolder, Guide>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ItemGuideBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(itemsList[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, PendingNewGuideActivity::class.java).apply {
                putExtra(PendingNewGuideActivity.GUIDE_KEY, itemsList[position])
            }
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                Pair(holder.binding().itemTitle, "titleTransition"),
                Pair(holder.binding().itemDescription, "descriptionTransition"),
            )
            it.context.startActivity(intent, options.toBundle())
        }
    }

    override fun getItemCount() = itemsList.size
}