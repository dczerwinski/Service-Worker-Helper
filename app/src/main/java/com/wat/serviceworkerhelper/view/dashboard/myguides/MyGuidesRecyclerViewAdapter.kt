package com.wat.serviceworkerhelper.view.dashboard.myguides

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ItemCategoryBinding
import com.wat.serviceworkerhelper.databinding.ItemGuideBinding
import com.wat.serviceworkerhelper.databinding.ItemGuideWithStatusBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.utils.MyRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide.SingleGuideActivity
import com.wat.serviceworkerhelper.view.dashboard.common.GuideViewHolder

class MyGuidesRecyclerViewAdapter(
    private val activity: Activity
) : MyRecyclerViewAdapter<RecyclerView.ViewHolder, Any>() {

    companion object {
        private const val TAG = "MyGuidesRVA"
    }

    private var categoriesList = ArrayList<Category>()
    private lateinit var recyclerView: RecyclerView

    fun setItems(allGuides: List<Guide>, currentUser: User) {
        val tempList = ArrayList<Guide>()
        categoriesList = ArrayList()

        // Created by current User
        val createdByUserCat = createCategory(
            currentUser.categories[0],
            allGuides,
            activity.getString(R.string.category_my_guides)
        )
        tempList.addAll(createdByUserCat.guides)
        categoriesList.add(createdByUserCat)

        // Favourites
        val favouritesCat = createCategory(
            currentUser.categories[1],
            allGuides,
            activity.getString(R.string.category_favorites)
        )
        categoriesList.add(favouritesCat)

        // Add user own categories
        for (i in 2 until currentUser.categories.size) {
            val cat = createCategory(
                currentUser.categories[i],
                allGuides
            )
            categoriesList.add(cat)
        }
        setUpAllList()
    }

    private fun createCategory(
        cat: User.Category,
        allGuides: List<Guide>,
        name: String = ""
    ): Category {
        if (name.isNotEmpty()) {
            cat.name = name
        }
        val guides = getGuidesByUIDs(cat.guidesUIDs, allGuides)
        return Category(cat, guides, 0, false)
    }

    private fun getGuidesByUIDs(
        guidesUIDs: List<String>,
        allGuides: List<Guide>
    ): ArrayList<Guide> {
        val result = ArrayList<Guide>()
        Log.i(TAG, "guidesUIDs = $guidesUIDs")
        allGuides.forEach {
            Log.i(TAG, "guide uid = ${it.uid}")
            if (guidesUIDs.contains(it.uid)) {
                result.add(it)
            }
        }
        Log.i(TAG, "result = $result")
        return result
    }

    private fun expand(category: User.Category) {
        var c: Category? = null
        for (cat in categoriesList) {
            if (cat.category == category) {
                c = cat
                break
            }
        }

        c!!.expanded = true
        val guidesCount = c.guides.size
        val tempList = ArrayList<Category>()
        for (cat in categoriesList) {
            if (cat.position > cat.position) {
                cat.position = cat.position + guidesCount
            }
            tempList.add(cat)
        }
        categoriesList = ArrayList(tempList)
        setUpAllList()
    }

    private fun rollUp(category: User.Category) {
        var c: Category? = null
        for (cat in categoriesList) {
            if (cat.category == category) {
                c = cat
                break
            }
        }

        c!!.expanded = false
        val guidesCount = c.guides.size
        val tempList = ArrayList<Category>()
        for (cat in categoriesList) {
            if (cat.position > c.position) {
                cat.position = cat.position - guidesCount
            }
            tempList.add(cat)
        }
        categoriesList = ArrayList(tempList)
        setUpAllList()
    }

    private fun setUpAllList() {
        val tempList = ArrayList<Any>()
        for (category in categoriesList) {
            tempList.add(category.category)
            category.position = tempList.size - 1
            if (category.expanded) {
                tempList.addAll(category.guides)
            }
        }

        setItems(tempList)
        recyclerView.startLayoutAnimation()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        Type.CATEGORY.ordinal -> {
            val binding = ItemCategoryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            CategoryViewHolder(binding)
        }
        Type.GUIDE.ordinal -> {
            val binding = ItemGuideBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            GuideViewHolder(binding)
        }
        Type.GUIDE_WITH_STATUS.ordinal -> {
            val binding = ItemGuideWithStatusBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            GuideWithStatusViewHolder(binding)
        }
        else -> throw IllegalStateException("Wrong type!")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (getItemViewType(position)) {
            Type.CATEGORY.ordinal -> {
                (holder as CategoryViewHolder).bind(itemsList[position] as User.Category)
                holder.binding.showMoreIcon.setOnClickListener {
                    if (holder.isExpanded) {
                        rollUp(itemsList[position] as User.Category)
                        holder.rollUp()
                    } else {
                        expand(itemsList[position] as User.Category)
                        holder.expand()
                    }
                }
            }
            Type.GUIDE.ordinal -> {
                (holder as GuideViewHolder).bind(itemsList[position] as Guide)
                holder.itemView.setOnClickListener {
                    val intent = Intent(it.context, SingleGuideActivity::class.java).apply {
                        putExtra(SingleGuideActivity.GUIDE_KEY, itemsList[position] as Guide)
                    }
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        Pair(holder.binding().itemTitle, "titleTransition"),
                        Pair(holder.binding().itemDescription, "descriptionTransition"),
                    )
                    it.context.startActivity(intent, options.toBundle())
                }
            }
            Type.GUIDE_WITH_STATUS.ordinal -> {
                (holder as GuideWithStatusViewHolder).bind(itemsList[position] as Guide)
                holder.itemView.setOnClickListener {
                    val intent = Intent(it.context, SingleGuideActivity::class.java).apply {
                        putExtra(SingleGuideActivity.GUIDE_KEY, itemsList[position] as Guide)
                    }
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        Pair(holder.getBinding().itemTitle, "titleTransition"),
                        Pair(holder.getBinding().itemDescription, "descriptionTransition"),
                    )
                    it.context.startActivity(intent, options.toBundle())
                }
            }
            else -> throw IllegalStateException("Wrong type!")
        }

    override fun getItemCount() = itemsList.size

    override fun getItemViewType(position: Int) = when {
        itemsList[position] is User.Category -> Type.CATEGORY.ordinal
        itemsList[position] is Guide -> {
            if ((itemsList[position] as Guide).guideStatus != Guide.Status.ADDED) {
                Type.GUIDE_WITH_STATUS.ordinal
            } else {
                Type.GUIDE.ordinal
            }
        }
        else -> throw IllegalStateException("Wrong item in allItemsList! type is ${itemsList[position]}")
    }

    private enum class Type {
        CATEGORY,
        GUIDE,
        GUIDE_WITH_STATUS
    }

    private data class Category(
        var category: User.Category,
        var guides: ArrayList<Guide>,
        var position: Int,
        var expanded: Boolean
    )
}