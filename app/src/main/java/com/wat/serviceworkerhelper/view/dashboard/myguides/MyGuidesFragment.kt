package com.wat.serviceworkerhelper.view.dashboard.myguides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel
import com.wat.serviceworkerhelper.view.dashboard.DashboardActivity
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.google.firebase.auth.FirebaseAuth

class MyGuidesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyGuidesRecyclerViewAdapter
    private lateinit var viewManager: GridLayoutManager
    private val database by lazy { AppRoomDatabase.getDatabase(requireContext()) }
    private val repository by lazy { GuideEntityRepository(database.guideDao()) }
    private val viewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(repository)
    }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }
    private var pair = Pair<List<Guide>?, User?>(null, null)
    private val mutableLiveData = MutableLiveData(pair)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_guides, container, false)

        adapter = MyGuidesRecyclerViewAdapter(requireActivity())
        viewModel
            .allGuides
            .observe(viewLifecycleOwner, { guides ->
                pair = Pair(guides, pair.second)
                mutableLiveData.postValue(pair)
            })
        usersViewModel
            .currentUser(FirebaseAuth.getInstance().currentUser!!.uid)
            .observe(viewLifecycleOwner, {
                pair = Pair(pair.first, it[0])
                mutableLiveData.postValue(pair)
            })
        mutableLiveData.observe(viewLifecycleOwner, {
            if (it.first != null && it.second != null) {
                adapter.setItems(pair.first!!, pair.second!!)
            }
        })

        viewManager = GridLayoutManager(requireContext(), 1)
        if (!DashboardSearchController.getInstance().equals(adapter)) {
            DashboardSearchController.getInstance().setCurrentAdapter(adapter)
        }

        recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@MyGuidesFragment.adapter
            addItemDecoration(ItemDecoration())
        }

        (requireActivity() as DashboardActivity).getFloatingButton().hide()
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyGuidesFragment()
    }
}