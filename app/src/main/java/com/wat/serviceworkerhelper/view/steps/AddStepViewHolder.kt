package com.wat.serviceworkerhelper.view.steps

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ItemAddStepBinding

class AddStepViewHolder(
    val binding: ItemAddStepBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun deletePhoto() {
        binding.addPhoto.setImageResource(R.drawable.ic_add_photo)
    }

    fun addPhoto(uri: Uri) {
        Picasso.get().load(uri).into(binding.addPhoto)
    }
}