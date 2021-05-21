package com.wat.serviceworkerhelper.view.steps

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ItemAddStepBinding
import com.wat.serviceworkerhelper.databinding.ItemStepBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StepsRecyclerViewAdapter(
    val activity: Activity?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList = ArrayList<Item>()
    private var PICK_IMAGE_CODE = -1

    fun setList(list: List<Guide.Step>) {
        itemList = ArrayList()
        list.forEach {
            itemList.add(
                Item(
                    it,
                    Uri.parse(it.photoUrl),
                    Type.STEP
                )
            )
        }
        notifyDataSetChanged()
    }

    fun setList(
        list: List<Guide.Step>,
        pickImageCode: Int
    ) {
        itemList = ArrayList()
        PICK_IMAGE_CODE = pickImageCode

        list.forEach {
            itemList.add(
                Item(
                    it,
                    null,
                    Type.STEP
                )
            )
        }
        itemList.add(
            Item(
                null,
                null,
                Type.ADD
            )
        )

        notifyDataSetChanged()
    }

    fun getItems(): ArrayList<Item> {
        val temp = ArrayList<Item>()
        itemList.forEach {
            if (it.type == Type.STEP) {
                temp.add(it)
            }
        }
        return temp
    }

    fun setPhotoUri(uri: Uri) {
        itemList.last().uri = uri
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        Type.STEP.viewType -> {
            val binding = ItemStepBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            StepViewHolder(binding)
        }
        Type.ADD.viewType -> {
            val binding = ItemAddStepBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            AddStepViewHolder(binding)
        }
        else -> throw IllegalArgumentException("Wrong viewType!")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (getItemViewType(position)) {
            Type.STEP.viewType -> {
                (holder as StepViewHolder).setUp(itemList[position].step!!, position + 1)
                if (PICK_IMAGE_CODE > 0) {
                    holder.binding.delete.visibility = View.VISIBLE
                    holder.binding.delete.setOnClickListener {
                        itemList.removeAt(position)
                        notifyDataSetChanged()
                    }
                } else {
                    holder.binding.delete.visibility = View.GONE
                }
            }

            Type.ADD.viewType -> {
                if (itemList[position].uri != null) {
                    (holder as AddStepViewHolder).addPhoto(itemList[position].uri!!)
                    holder.binding.addPhoto.setOnClickListener {
                        itemList[position].uri = null
                        notifyDataSetChanged()
                    }
                } else {
                    (holder as AddStepViewHolder).deletePhoto()
                    holder.binding.addPhoto.setOnClickListener {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            activity!!,
                            Intent.createChooser(intent, activity.getString(R.string.choose_photo)),
                            PICK_IMAGE_CODE,
                            null
                        )
                    }
                }

                holder.binding.addIt.setOnClickListener {
                    if (holder.binding.stepContent.text.toString().isNotEmpty()) {
                        val uri = itemList.last().uri
                        itemList.removeLast()
                        itemList.add(
                            Item(
                                Guide.Step(
                                    holder.binding.stepContent.text.toString(),
                                    uri.toString()
                                ),
                                uri,
                                Type.STEP
                            )
                        )
                        holder.binding.stepContent.setText("")
                        itemList.add(
                            Item(
                                null,
                                null,
                                Type.ADD
                            )
                        )
                        notifyDataSetChanged()
                        GlobalScope.launch {
                            delay(50L)
                            Handler(Looper.getMainLooper()).post {
                                holder.binding.stepContent.requestFocus()
                                holder.binding.stepContent.isFocusableInTouchMode = true
                                val inputMetManager = it
                                    .context
                                    .getSystemService(
                                        Context.INPUT_METHOD_SERVICE
                                    ) as InputMethodManager
                                inputMetManager.showSoftInput(
                                    holder.binding.stepContent,
                                    InputMethodManager.SHOW_FORCED
                                )
                            }
                        }
                    } else {
                        Toast.makeText(
                            holder.itemView.context,
                            R.string.guide_content_is_empty,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            else -> throw IllegalArgumentException("Wrong viewType!")
        }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int) = itemList[position].type.viewType

    data class Item(
        var step: Guide.Step?,
        var uri: Uri?,
        var type: Type
    )

    enum class Type(
        val viewType: Int
    ) {
        STEP(0),
        ADD(1)
    }
}