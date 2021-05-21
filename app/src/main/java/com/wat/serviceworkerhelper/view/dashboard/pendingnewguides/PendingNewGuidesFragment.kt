package com.wat.serviceworkerhelper.view.dashboard.pendingnewguides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.databinding.FragmentPendingNewGuidesBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.wat.serviceworkerhelper.view.dashboard.DashboardActivity
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel

class PendingNewGuidesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PendingNewGuidesRecyclerViewAdapter
    private lateinit var viewManager: LinearLayoutManager
    private val database by lazy { AppRoomDatabase.getDatabase(requireContext()) }
    private val repository by lazy { GuideEntityRepository(database.guideDao()) }
    private val viewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPendingNewGuidesBinding.inflate(inflater, container, false)

        adapter = PendingNewGuidesRecyclerViewAdapter(requireActivity())
        viewModel.allPendingGuides.observe(viewLifecycleOwner, { guides ->
            guides?.let {
                adapter.setItems(it)
                recyclerView.startLayoutAnimation()
            }
        })

        viewManager = LinearLayoutManager(requireContext())
        if (!DashboardSearchController.getInstance().equals(adapter)) {
            DashboardSearchController.getInstance().setCurrentAdapter(adapter)
        }

        recyclerView = binding.opinionsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@PendingNewGuidesFragment.adapter
            addItemDecoration(ItemDecoration())
        }

        (requireActivity() as DashboardActivity).getFloatingButton().hide()

        return binding.root
    }
}