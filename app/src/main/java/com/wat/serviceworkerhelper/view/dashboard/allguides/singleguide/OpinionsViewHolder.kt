package com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.squareup.picasso.Picasso

class OpinionsViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val displayName = itemView.findViewById<TextView>(R.id.displayName)
    private val ratingBar = itemView.findViewById<RatingBar>(R.id.ratingBar)
    private val date = itemView.findViewById<TextView>(R.id.date)
    private val mainOpinion = itemView.findViewById<TextView>(R.id.mainOpinion)
    private val avatar = itemView.findViewById<ImageView>(R.id.avatar)

    fun bind(opinion: Guide.Opinion, user: User?) {
        if (user == null) {
            displayName.text = itemView.context.getString(R.string.loading)
        } else {
            displayName.text = user.displayName
            if (user.photoURL.isNotEmpty()) {
                Picasso.get().load(user.photoURL).into(avatar)
            }
        }
        ratingBar.rating = opinion.rate
        date.text = opinion.date
        mainOpinion.text = opinion.opinion
    }
}