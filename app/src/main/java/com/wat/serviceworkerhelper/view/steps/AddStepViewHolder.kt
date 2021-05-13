package com.wat.serviceworkerhelper.view.steps

import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso

class AddStepViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    val stepContent: EditText = itemView.findViewById(R.id.stepContent)
    val addPhotoButton: ImageView = itemView.findViewById(R.id.addPhoto)
    val addItButton: FloatingActionButton = itemView.findViewById(R.id.addIt)

    fun deletePhoto() {
        addPhotoButton.setImageResource(R.drawable.ic_add_photo)
    }

    fun addPhoto(uri: Uri) {
        Picasso.get().load(uri).into(addPhotoButton)
    }
}