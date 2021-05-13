package com.wat.serviceworkerhelper.view.dashboard.allguides

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide.SingleGuideActivity
import com.wat.serviceworkerhelper.view.dashboard.common.GuideViewHolder
import com.wat.serviceworkerhelper.utils.MyRecyclerViewAdapter

class AllGuidesRecyclerViewAdapter(
    private val activity: Activity
) : MyRecyclerViewAdapter<GuideViewHolder, Guide>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val inflater: LayoutInflater? = LayoutInflater.from(parent.context)
        val view: View = inflater!!.inflate(R.layout.item_guide, parent, false)

        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(itemsList[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, SingleGuideActivity::class.java).apply {
                putExtra(SingleGuideActivity.GUIDE_KEY, itemsList[position])
            }
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                Pair(holder.titleTV, "titleTransition"),
                Pair(holder.descriptionTV, "descriptionTransition"),
            )
            it.context.startActivity(intent, options.toBundle())
        }
    }

    override fun getItemCount() = itemsList.size

    companion object {
        private const val TAG = "GuidesRVA"
    }
}