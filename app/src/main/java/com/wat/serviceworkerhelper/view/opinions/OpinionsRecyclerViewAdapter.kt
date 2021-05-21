package com.wat.serviceworkerhelper.view.opinions

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.databinding.ItemLoadingBinding
import com.wat.serviceworkerhelper.databinding.ItemNoItemsBinding
import com.wat.serviceworkerhelper.databinding.ItemOpinionBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide.SingleGuideActivity
import kotlin.math.min

class OpinionsRecyclerViewAdapter(
    private val activity: SingleGuideActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
        private const val VIEW_TYPE_NO_ITEMS = 2

        private const val TAG = "OpinionsRVA"
    }

    private var opinionsList = ArrayList<Guide.Opinion?>()
    private var tempOpinionsList = ArrayList<Guide.Opinion>()
    private var isLoading = false
    private var hasOpinions = false
    private var allUsers = ArrayList<User>()

    fun setList(opinionsMap: HashMap<String, Guide.Opinion>) {
        opinionsList = ArrayList(listOf(null))
        if (opinionsMap.isNotEmpty()) {
            hasOpinions = true
            notifyDataSetChanged()
            Thread {
                tempOpinionsList = ArrayList(opinionsMap.values)
                val number = min(tempOpinionsList.size, 6)
                opinionsList.remove(null)
                if (number > 0) {
                    for (i in 0 until number) {
                        opinionsList.add(tempOpinionsList[0])
                        tempOpinionsList.removeAt(0)
                    }
                    if (tempOpinionsList.size > 0) {
                        opinionsList.add(null)
                    }
                    activity.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
            }.start()
        } else {
            hasOpinions = false
            tempOpinionsList = ArrayList()
            isLoading = false
            notifyDataSetChanged()
        }
    }

    fun setUsersList(users: ArrayList<User>) {
        allUsers = users
        notifyDataSetChanged()
    }

    fun addMore() {
        if (tempOpinionsList.isNotEmpty() && !isLoading) {
            isLoading = true
            Thread {
                val number = min(tempOpinionsList.size, 6)
                opinionsList.remove(null)
                Log.d(TAG, "addMore adding $number of opinions")
                if (number > 0) {
                    for (i in 0 until number) {
                        opinionsList.add(tempOpinionsList[0])
                        tempOpinionsList.removeAt(0)
                    }
                    opinionsList.add(null)
                    activity.runOnUiThread {
                        isLoading = false
                        notifyDataSetChanged()
                    }
                }
            }.start()
        }
    }

    private fun getUserInfo(uid: String): User? {
        allUsers.forEach {
            if (it.uid == uid) {
                return it
            }
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_ITEM -> {
            val binding = ItemOpinionBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            OpinionsViewHolder(binding)
        }
        VIEW_TYPE_LOADING -> {
            val binding = ItemLoadingBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(binding)
        }
        VIEW_TYPE_NO_ITEMS -> {
            val binding = ItemNoItemsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            NoItemsViewHolder(binding)
        }
        else -> {
            throw IllegalArgumentException("Wrong view type!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> {
                holder.progressBar.isIndeterminate = true
            }
            is OpinionsViewHolder -> {
                holder.bind(
                    opinionsList[position]!!,
                    getUserInfo(opinionsList[position]!!.creatorUID)
                )
            }
        }
    }

    override fun getItemCount() = opinionsList.size

    override fun getItemViewType(position: Int): Int {
        return when {
            !hasOpinions -> VIEW_TYPE_NO_ITEMS
            opinionsList[position] == null -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }
}