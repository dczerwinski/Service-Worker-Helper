package com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R

class LoadingViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
}