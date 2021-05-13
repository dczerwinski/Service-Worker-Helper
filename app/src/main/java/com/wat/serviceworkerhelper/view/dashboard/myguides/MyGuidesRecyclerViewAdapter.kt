package com.wat.serviceworkerhelper.view.dashboard.myguides

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide.SingleGuideActivity
import com.wat.serviceworkerhelper.view.dashboard.common.GuideViewHolder
import com.wat.serviceworkerhelper.utils.MyRecyclerViewAdapter

class MyGuidesRecyclerViewAdapter(
    private val activity: Activity
) : MyRecyclerViewAdapter<RecyclerView.ViewHolder, Guide>() {

    companion object {
        private const val TAG = "MyGuidesRVA"
    }

    private var categoriesList = ArrayList<Category>()
    private var allItemsList = ArrayList<Any>()
    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater? = LayoutInflater.from(parent.context)
        val view: View
        return when (viewType) {
            Type.CATEGORY.ordinal -> {
                view = inflater!!.inflate(R.layout.item_category, parent, false)
                CategoryViewHolder(view)
            }
            Type.GUIDE.ordinal -> {
                view = inflater!!.inflate(R.layout.item_guide, parent, false)
                GuideViewHolder(view)
            }
            Type.GUIDE_WITH_STATUS.ordinal -> {
                view = inflater!!.inflate(R.layout.item_guide_with_status, parent, false)
                GuideWithStatusViewHolder(view)
            }
            else -> throw IllegalStateException("Wrong type!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Type.CATEGORY.ordinal -> {
                (holder as CategoryViewHolder).bind(allItemsList[position] as User.Category)
                holder.showMoreIcon.setOnClickListener {
                    if (holder.isExpanded) {
                        rollUp(allItemsList[position] as User.Category)
                        holder.rollUp()
                    } else {
                        expand(allItemsList[position] as User.Category)
                        holder.expand()
                    }
                }
            }
            Type.GUIDE.ordinal -> {
                (holder as GuideViewHolder).bind(allItemsList[position] as Guide)
                holder.itemView.setOnClickListener {
                    val intent = Intent(it.context, SingleGuideActivity::class.java).apply {
                        putExtra(SingleGuideActivity.GUIDE_KEY, allItemsList[position] as Guide)
                    }
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        Pair(holder.titleTV, "titleTransition"),
                        Pair(holder.descriptionTV, "descriptionTransition"),
                    )
                    it.context.startActivity(intent, options.toBundle())
                }
            }
            Type.GUIDE_WITH_STATUS.ordinal -> {
                (holder as GuideWithStatusViewHolder).bind(allItemsList[position] as Guide)
                holder.itemView.setOnClickListener {
                    val intent = Intent(it.context, SingleGuideActivity::class.java).apply {
                        putExtra(SingleGuideActivity.GUIDE_KEY, allItemsList[position] as Guide)
                    }
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        Pair(holder.titleTV, "titleTransition"),
                        Pair(holder.descriptionTV, "descriptionTransition"),
                    )
                    it.context.startActivity(intent, options.toBundle())
                }
            }
            else -> throw IllegalStateException("Wrong type!")
        }
    }

    override fun getItemCount() = allItemsList.size

    override fun getItemViewType(position: Int) = when {
        allItemsList[position] is User.Category -> Type.CATEGORY.ordinal
        allItemsList[position] is Guide -> {
            if ((allItemsList[position] as Guide).guideStatus != Guide.Status.ADDED) {
                Type.GUIDE_WITH_STATUS.ordinal
            } else {
                Type.GUIDE.ordinal
            }
        }
        else -> throw IllegalStateException("Wrong item in allItemsList! type is ${allItemsList[position]}")
    }

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
        tempList.addAll(favouritesCat.guides)
        categoriesList.add(favouritesCat)

        // Add user own categories
        for (i in 2 until currentUser.categories.size) {
            val cat = createCategory(
                currentUser.categories[i],
                allGuides
            )
            tempList.addAll(cat.guides)
            categoriesList.add(cat)
        }
        setUpAllList()
        setItems(tempList)
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
        return Category(cat, guides, 0, true)
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
            if (cat.postion > cat.postion) {
                cat.postion = cat.postion + guidesCount
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
            if (cat.postion > c.postion) {
                cat.postion = cat.postion - guidesCount
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
            category.postion = tempList.size - 1
            if (category.expanded) {
                tempList.addAll(category.guides)
            }
        }

        allItemsList = ArrayList(tempList)
        notifyDataSetChanged()
        recyclerView.startLayoutAnimation()
    }

    private enum class Type {
        CATEGORY,
        GUIDE,
        GUIDE_WITH_STATUS
    }

    private data class Category(
        var category: User.Category,
        var guides: ArrayList<Guide>,
        var postion: Int,
        var expanded: Boolean
    )
}