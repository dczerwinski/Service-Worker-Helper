package com.wat.serviceworkerhelper.view.dashboard.allguides

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.databinding.FragmentAllGuidesBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.wat.serviceworkerhelper.view.dashboard.DashboardActivity
import com.wat.serviceworkerhelper.view.dashboard.addguide.AddGuideActivity
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel

class AllGuidesFragment : Fragment() {

    companion object {
        private const val TAG = "AllGuidesFragment"
    }

    private lateinit var binding: FragmentAllGuidesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AllGuidesRecyclerViewAdapter
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
        binding = FragmentAllGuidesBinding.inflate(inflater, container, false)

        adapter = AllGuidesRecyclerViewAdapter(requireActivity())
        viewModel.allAddedGuides.observe(viewLifecycleOwner, { guides ->
            adapter.setItems(guides)
            recyclerView.startLayoutAnimation()
            Log.i(TAG, "setItems")
        })

        viewManager = LinearLayoutManager(requireContext())
        if (!DashboardSearchController.getInstance().equals(adapter)) {
            DashboardSearchController.getInstance().setCurrentAdapter(adapter)
        }

        recyclerView = binding.opinionsRecyclerView.apply {
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

        return binding.root
    }
}