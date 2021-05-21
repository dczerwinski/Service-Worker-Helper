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
import com.wat.serviceworkerhelper.databinding.FragmentManageUsersBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.wat.serviceworkerhelper.view.dashboard.DashboardActivity
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel

class ManageUsersFragment : Fragment() {

    private lateinit var binding: FragmentManageUsersBinding
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
    ): View {
        binding = FragmentManageUsersBinding.inflate(inflater, container, false)

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

        recyclerView = binding.opinionsRecyclerView.apply {
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

        return binding.root
    }
}