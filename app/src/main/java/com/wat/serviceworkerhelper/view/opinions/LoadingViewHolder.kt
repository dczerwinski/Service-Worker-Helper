package com.wat.serviceworkerhelper.view.opinions

import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.databinding.ItemLoadingBinding

class LoadingViewHolder(
    binding: ItemLoadingBinding
) : RecyclerView.ViewHolder(binding.root) {

    val progressBar = binding.progressBar
}