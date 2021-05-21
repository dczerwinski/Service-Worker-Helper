package com.wat.serviceworkerhelper.view.dashboard.manageusers

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wat.serviceworkerhelper.databinding.ItemManageUsersBinding
import com.wat.serviceworkerhelper.model.entities.User

class ManageUsersViewHolder(
    val binding: ItemManageUsersBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.displayName.text = user.displayName
        binding.email.text = user.email
        Picasso.get().load(user.photoURL).into(binding.avatar)
    }
}