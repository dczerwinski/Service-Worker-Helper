package com.wat.serviceworkerhelper.view.dashboard.allguides

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import com.wat.serviceworkerhelper.view.dashboard.DashboardActivity
import com.wat.serviceworkerhelper.view.dashboard.addguide.AddGuideActivity
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.wat.serviceworkerhelper.utils.NetworkUtils

class AllGuidesFragment : Fragment() {

    companion object {
        private const val TAG = "AllGuidesFragment"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AllGuidesRecyclerViewAdapter
    private lateinit var viewManager: GridLayoutManager
    private val database by lazy { AppRoomDatabase.getDatabase(requireContext()) }
    private val repository by lazy { GuideEntityRepository(database.guideDao()) }
    private val viewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(repository)
    }
    private var isItFirstOpen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_all_guides, container, false)

        adapter = AllGuidesRecyclerViewAdapter(requireActivity())
        viewModel.allAddedGuides.observe(viewLifecycleOwner, { guides ->
            adapter.setItems(guides)
            recyclerView.startLayoutAnimation()
            Log.i(TAG, "setItems")
        })

        viewManager = GridLayoutManager(requireContext(), 1)
        if (!DashboardSearchController.getInstance().equals(adapter)) {
            DashboardSearchController.getInstance().setCurrentAdapter(adapter)
        }

        recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@AllGuidesFragment.adapter
            addItemDecoration(ItemDecoration())
        }

        (requireActivity() as DashboardActivity).getFloatingButton().apply {
            setOnClickListener {
                val intent = Intent(it.context, AddGuideActivity::class.java)
                startActivity(intent)
            }
            show()
        }

        return root
    }
}