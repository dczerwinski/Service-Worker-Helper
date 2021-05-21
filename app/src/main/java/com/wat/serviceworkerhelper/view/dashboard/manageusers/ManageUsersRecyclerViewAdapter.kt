package com.wat.serviceworkerhelper.view.dashboard.manageusers

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wat.serviceworkerhelper.databinding.ItemManageUsersBinding
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.utils.MyRecyclerViewAdapter

class ManageUsersRecyclerViewAdapter(
    private val activity: Activity
) : MyRecyclerViewAdapter<ManageUsersViewHolder, User>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageUsersViewHolder {
        val binding = ItemManageUsersBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ManageUsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ManageUsersViewHolder, position: Int) {
        holder.bind(itemsList[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, UserActivity::class.java).apply {
                putExtra(UserActivity.USER_KEY, itemsList[position])
            }

            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                Pair(holder.binding.displayName, "displayNameTransition"),
                Pair(holder.binding.email, "emailTransition"),
                Pair(holder.binding.cardView, "imageTransition")
            )

            activity.startActivity(intent, options.toBundle())
        }
    }

    override fun getItemCount() = itemsList.size
}