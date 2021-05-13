package com.wat.serviceworkerhelper.view.dashboard.manageusers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.User
import com.squareup.picasso.Picasso

class ManageUsersViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val photo: ImageView = itemView.findViewById(R.id.avatar)
    val displayName: TextView = itemView.findViewById(R.id.displayName)
    val email: TextView = itemView.findViewById(R.id.email)
    val cardView: CardView = itemView.findViewById(R.id.cardView)

    fun bind(user: User) {
        displayName.text = user.displayName
        email.text = user.email
        Picasso.get().load(user.photoURL).into(photo)
    }
}