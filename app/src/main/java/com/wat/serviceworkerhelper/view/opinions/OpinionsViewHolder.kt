package com.wat.serviceworkerhelper.view.opinions

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ItemOpinionBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.utils.NetworkUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OpinionsViewHolder(
    private val binding: ItemOpinionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(opinion: Guide.Opinion, user: User?) {
        if (user == null) {
            Picasso
                .get()
                .load(binding.root.context.getString(R.string.defaultAvatarUrl))
                .into(binding.avatar)
            val loadingString = binding.root.context.getString(R.string.loading)
            binding.displayName.text = loadingString
            if (NetworkUtils.isOnline(binding.root.context.applicationContext)) {
                binding.displayName.text = binding.root.context.getString(R.string.deleted_user)
            } else {
                GlobalScope.launch {
                    // Wait to load user
                    delay(30000L)
                    Handler(Looper.getMainLooper()).post {
                        if (binding.displayName.text == loadingString) {
                            binding.displayName.text =
                                binding.root.context.getString(R.string.deleted_user)
                        }
                    }
                }
            }
        } else {
            binding.displayName.text = user.displayName
            if (user.photoURL.isNotEmpty()) {
                Picasso
                    .get()
                    .load(user.photoURL)
                    .into(binding.avatar)
            }
        }
        binding.ratingBar.rating = opinion.rate
        binding.date.text = opinion.date
        binding.mainOpinion.text = opinion.opinion
    }
}