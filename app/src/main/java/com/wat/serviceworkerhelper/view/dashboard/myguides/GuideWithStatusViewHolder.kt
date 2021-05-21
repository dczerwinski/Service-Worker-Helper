package com.wat.serviceworkerhelper.view.dashboard.myguides

import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ItemGuideWithStatusBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.utils.StepsUtils

class GuideWithStatusViewHolder(
    private val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val TAG = "GuidesSViewHolder"
    }

    fun bind(guide: Guide) {
        if (binding !is ItemGuideWithStatusBinding)
            throw IllegalArgumentException("Wrong binding type!")
        var info = ""
        when (guide.guideStatus) {
            Guide.Status.PENDING -> {
                binding.layoutStatus.background = ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.radius_pending,
                    null
                )
                binding.itemStatus.text = itemView.context.getString(R.string.status_pending)
                info = itemView.context.getString(R.string.status_pending)
            }
            Guide.Status.REPORTED -> {
                binding.layoutStatus.background = ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.radius_reported,
                    null
                )
                binding.itemStatus.text = itemView.context.getString(R.string.status_reported)
                info = itemView.context.getString(R.string.status_reported)
            }
            else -> Log.e(TAG, "Wrong type!")
        }

        binding.iconInfo.setOnClickListener {
            showInfo(info)
        }
        binding.layoutStatus.setOnClickListener {
            showInfo(info)
        }

        binding.itemTitle.text = guide.title
        binding.itemDescription.text = StepsUtils.toString(guide.steps)
        if (guide.opinions.isEmpty()) {
            binding.itemRating.text = "N/A"
        } else {
            binding.itemRating.text = String.format("%.1f", guide.rate)
        }
    }

    private fun showInfo(info: String) {
        Toast.makeText(itemView.context, info, Toast.LENGTH_SHORT).show()
    }

    fun getBinding(): ItemGuideWithStatusBinding = binding as ItemGuideWithStatusBinding
}