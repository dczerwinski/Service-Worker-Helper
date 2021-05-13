package com.wat.serviceworkerhelper.view.dashboard.myguides

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.view.dashboard.common.GuideViewHolder

class GuideWithStatusViewHolder(
    itemView: View
) : GuideViewHolder(itemView) {

    companion object {
        private const val TAG = "GuidesSViewHolder"
    }

    private val itemStatus: TextView = itemView.findViewById(R.id.itemStatus)
    private val layoutStatus: LinearLayout = itemView.findViewById(R.id.layoutStatus)
    private val iconInfo: ImageView = itemView.findViewById(R.id.iconInfo)

    override fun bind(guide: Guide) {
        var info = ""
        when (guide.guideStatus) {
            Guide.Status.PENDING -> {
                layoutStatus.background = ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.radius_pending,
                    null
                )
                itemStatus.text = itemView.context.getString(R.string.status_pending)
                info = itemView.context.getString(R.string.status_pending)
            }
            Guide.Status.REPORTED -> {
                layoutStatus.background = ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.radius_reported,
                    null
                )
                itemStatus.text = itemView.context.getString(R.string.status_reported)
                info = itemView.context.getString(R.string.status_reported)
            }
            else -> Log.e(TAG, "Wrong type!")
        }

        iconInfo.setOnClickListener {
            showInfo(info)
        }
        layoutStatus.setOnClickListener {
            showInfo(info)
        }

        super.bind(guide)
    }

    private fun showInfo(info: String) {
        Toast.makeText(itemView.context, info, Toast.LENGTH_SHORT).show()
    }
}