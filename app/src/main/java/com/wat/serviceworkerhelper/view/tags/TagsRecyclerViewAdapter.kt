package com.wat.serviceworkerhelper.view.tags

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R

class TagsRecyclerViewAdapter(
    private val isEditable: Boolean,
    private val onTagAddListener: OnTagAddListener?
) : RecyclerView.Adapter<TagViewHolder>() {

    private val tagsList = ArrayList<String>()

    fun setList(tags: ArrayList<String>) {
        tagsList.clear()
        tagsList.addAll(tags)
        notifyDataSetChanged()
    }

    fun addToList(tag: String) {
        tagsList.add(tag)
        notifyDataSetChanged()
    }

    fun getTags(): ArrayList<String> {
        return tagsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater: LayoutInflater? = LayoutInflater.from(parent.context)
        val view = inflater!!.inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.setUp(tagsList[position], isEditable)
        if (isEditable) {
            holder.xIcon.setOnClickListener {
                tagsList.removeAt(position)
                notifyDataSetChanged()
                onTagAddListener?.onTagAdd()
            }
        } else {
            holder.xIcon.visibility = View.GONE
        }
    }

    override fun getItemCount() = tagsList.size
}