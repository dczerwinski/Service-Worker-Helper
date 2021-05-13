package com.wat.serviceworkerhelper.view.dashboard.pendingnewguides

import android.os.Bundle
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
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.utils.ItemDecoration

class PendingNewGuidesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PendingNewGuidesRecyclerViewAdapter
    private lateinit var viewManager: GridLayoutManager
    private val database by lazy { AppRoomDatabase.getDatabase(requireContext()) }
    private val repository by lazy { GuideEntityRepository(database.guideDao()) }
    private val viewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater
            .inflate(R.layout.fragment_pending_new_guides, container, false)

        adapter = PendingNewGuidesRecyclerViewAdapter(requireActivity())
        viewModel.allPendingGuides.observe(viewLifecycleOwner, { guides ->
            guides?.let {
                adapter.setItems(it)
                recyclerView.startLayoutAnimation()
            }
        })

        viewManager = GridLayoutManager(requireContext(), 1)
        if (!DashboardSearchController.getInstance().equals(adapter)) {
            DashboardSearchController.getInstance().setCurrentAdapter(adapter)
        }

        recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@PendingNewGuidesFragment.adapter
            addItemDecoration(ItemDecoration())
        }

        (requireActivity() as DashboardActivity).getFloatingButton().hide()

        return root
    }

    companion object {
        fun newInstance() = PendingNewGuidesFragment()
    }
}