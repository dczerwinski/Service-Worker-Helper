package com.wat.serviceworkerhelper.view.steps

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ItemStepBinding
import com.wat.serviceworkerhelper.model.entities.Guide

class StepViewHolder(
    val binding: ItemStepBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setUp(step: Guide.Step, number: Int) {
        binding.itemContent.text = itemView.context.getString(R.string.step, number, step.content)
        if (step.photoUrl.isNotEmpty()) {
            binding.photo.visibility = View.VISIBLE
            val uri = Uri.parse(step.photoUrl)
            Picasso.get().load(uri).into(binding.photo)
            binding.photo.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    setDataAndType(uri, "image/*")
                }
                itemView.context.startActivity(intent)
            }
        } else {
            binding.photo.visibility = View.GONE
        }
    }
}