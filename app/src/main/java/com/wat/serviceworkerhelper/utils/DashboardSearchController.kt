package com.wat.serviceworkerhelper.utils

import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView

class DashboardSearchController {

    @Volatile
    private var adapter: MyRecyclerViewAdapter<RecyclerView.ViewHolder, Any>? = null
    private var searchView: SearchView? = null

    fun setCurrentAdapter(adapter: Any) {
        synchronized(this) {
            this.adapter = adapter as MyRecyclerViewAdapter<RecyclerView.ViewHolder, Any>
            setSearchViewListener()
        }
    }

    fun setSearchView(searchView: SearchView) {
        Log.i(TAG, "setSearchView")
        this.searchView = searchView
        setSearchViewListener()
    }

    private fun setSearchViewListener() {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }
        })
    }

    override fun equals(other: Any?): Boolean {
        if (adapter == other) return true
        return false
    }

    override fun hashCode(): Int {
        var result = adapter?.hashCode() ?: 0
        result = 31 * result + searchView.hashCode()
        return result
    }

    companion object {
        private const val TAG = "DSC"

        @Volatile
        private var INSTANCE: DashboardSearchController? = null

        fun getInstance(): DashboardSearchController {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }

            synchronized(this) {
                val i = DashboardSearchController()
                INSTANCE = i
                return i
            }
        }
    }
}