package com.wat.serviceworkerhelper.view.dashboard.manageusers

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.utils.MyRecyclerViewAdapter

class ManageUsersRecyclerViewAdapter(
    private val activity: Activity
) : MyRecyclerViewAdapter<ManageUsersViewHolder, User>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageUsersViewHolder {
        val inflater: LayoutInflater? = LayoutInflater.from(parent.context)
        val view: View = inflater!!.inflate(R.layout.item_manage_users, parent, false)

        return ManageUsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageUsersViewHolder, position: Int) {
        holder.bind(itemsList[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, UserActivity::class.java).apply {
                putExtra(UserActivity.USER_KEY, itemsList[position])
            }

            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                Pair(holder.displayName, "displayNameTransition"),
                Pair(holder.email, "emailTransition"),
                Pair(holder.cardView, "imageTransition")
            )

            activity.startActivity(intent, options.toBundle())
        }
    }

    override fun getItemCount() = itemsList.size
}