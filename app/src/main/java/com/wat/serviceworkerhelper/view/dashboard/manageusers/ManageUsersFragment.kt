package com.wat.serviceworkerhelper.view.dashboard.manageusers

import android.content.Intent
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
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel
import com.wat.serviceworkerhelper.view.dashboard.DashboardActivity
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.utils.ItemDecoration

class ManageUsersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ManageUsersRecyclerViewAdapter
    private lateinit var viewManager: GridLayoutManager
    private val database by lazy { AppRoomDatabase.getDatabase(requireContext()) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_manage_users, container, false)

        adapter = ManageUsersRecyclerViewAdapter(requireActivity())
        usersViewModel.allUsers.observe(viewLifecycleOwner, { users ->
            users.let {
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
            adapter = this@ManageUsersFragment.adapter
            addItemDecoration(ItemDecoration())
        }

        (requireActivity() as DashboardActivity).getFloatingButton().apply {
            setOnClickListener {
                val intent = Intent(it.context, AddUserActivity::class.java)
                startActivity(intent)
            }
            show()
        }

        return root
    }

    companion object {
        fun newInstance() = ManageUsersFragment()
    }
}