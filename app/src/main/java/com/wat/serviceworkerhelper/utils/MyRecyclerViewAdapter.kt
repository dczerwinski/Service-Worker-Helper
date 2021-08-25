package com.wat.serviceworkerhelper.utils

import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.model.entities.User
import java.util.*
import kotlin.collections.ArrayList

abstract class MyRecyclerViewAdapter<VH : RecyclerView.ViewHolder?, I>
    : RecyclerView.Adapter<VH>(), Filterable {

    /**
     * Recyclerview needs two lists, one for storing all guides and one for
     * storing the guides which will be displayed (already filtered).
     */
    protected var itemsList = ArrayList<I>()
    protected var itemsListAll = ArrayList<I>()
    private var filter = object : Filter() {

        /**
         * Invoked in a worker thread to filter the data according to the
         * constraint. Subclasses must implement this method to perform the
         * filtering operation. Results computed by the filtering operation
         * must be returned as a FilterResults that will then be published in
         * the UI thread through publishResults(CharSequence, FilterResults).
         *
         *
         * @param constraint The constraint used to filter the data.
         * @return The results of the filtering operation.
         */
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<I>()
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(itemsListAll)
            } else {
                for (item in itemsListAll) {
                    if (item.toString()
                            .toLowerCase(Locale.getDefault())
                            .contains(constraint.toString().toLowerCase(Locale.getDefault()))
                    ) {
                        filteredList.add(item)
                    } else if (item is User.Category) {
                        filteredList.add(item)
                    }
                }
            }

            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        /**
         * Invoked in the UI thread to publish the filtering results in the
         * user interface. Clears current list of filtered items and add all
         * items from FilterResult object. Then notify UI about changes.
         * This method is called on UI thread by default.
         *
         * @param constraint The constraint used to filter the data.
         * @param results The results of the filtering operation.
         * @suppress TODO Idk how to check it.
         */
        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            itemsList.clear()
            itemsList.addAll(results?.values as Collection<I>)
            notifyDataSetChanged()
        }
    }

    /**
     * Called when view, which has recyclerview, want to set items of it.
     * @param guides List of items that will be displayed in recycler view.
     */
    open fun setItems(guides: List<I>) {
        itemsList = ArrayList(guides)
        itemsListAll = ArrayList(guides)
        notifyDataSetChanged()
    }

    /**
     * @return Returns filter attached to recycler view.
     */
    override fun getFilter() = filter
}