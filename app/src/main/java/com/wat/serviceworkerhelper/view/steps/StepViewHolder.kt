package com.wat.serviceworkerhelper.view.steps

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.Guide
import com.squareup.picasso.Picasso

class StepViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val itemContent: TextView = itemView.findViewById(R.id.itemContent)
    private val itemPhoto: ImageView = itemView.findViewById(R.id.photo)
    val deleteButton: ImageView = itemView.findViewById(R.id.delete)

    fun setUp(step: Guide.Step, number: Int) {
        itemContent.text = itemView.context.getString(R.string.step, number, step.content)
        if (step.photoUrl.isNotEmpty()) {
            itemPhoto.visibility = View.VISIBLE
            val uri = Uri.parse(step.photoUrl)
            Picasso.get().load(uri).into(itemPhoto)
            itemPhoto.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    setDataAndType(uri, "image/*")
                }
                itemView.context.startActivity(intent)
            }
        } else {
            itemPhoto.visibility = View.GONE
        }
    }
}