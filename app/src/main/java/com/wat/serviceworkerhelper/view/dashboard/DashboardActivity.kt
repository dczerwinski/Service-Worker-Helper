package com.wat.serviceworkerhelper.view.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ActivityDashboardBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.utils.DashboardSearchController
import com.wat.serviceworkerhelper.view.login.LoginActivity
import com.wat.serviceworkerhelper.view.login.LoginActivity.Companion.USER_EXTRA_KEY
import com.wat.serviceworkerhelper.view.settings.SettingsActivity
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel

class DashboardActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DashboardActivity"
        private const val USER_KEY = "USER_KEY"
    }

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var avatarImageView: ImageView

    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }

    fun getFloatingButton() = binding.appBarDashboard.fab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        user = intent.extras!!.get(USER_EXTRA_KEY) as User

        setSupportActionBar(binding.appBarDashboard.toolbar)

        val headerLayout = binding.navView.getHeaderView(0)
        nameTextView = headerLayout.findViewById(R.id.nameTextView)
        emailTextView = headerLayout.findViewById(R.id.emailTextView)
        avatarImageView = headerLayout.findViewById(R.id.avatarImageView)
        val settingsButton = headerLayout.findViewById<ImageView>(R.id.settingsButton)

        usersViewModel.allUsers.observe(this, { users ->
            for (user in users) {
                if (user.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                    if (user != this@DashboardActivity.user) {
                        this@DashboardActivity.user = user
                        setUpUI()
                        resetUI()
                    }
                }
            }
        })

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra(USER_KEY, this.user)
            startActivity(intent)
        }

        binding.signOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        setUpUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_box_menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        DashboardSearchController.getInstance().setSearchView(searchView)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setUpUI() {
        navController = findNavController(R.id.nav_host_fragment)
        nameTextView.text = user.displayName
        emailTextView.text = user.email
        Picasso.get().load(user.photoURL).into(avatarImageView)

        if (user.userType == User.Type.ADMIN) {
            Log.i(TAG, "User is admin")
            binding.navView.inflateMenu(R.menu.activity_dashboard_drawer_admin)
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_admin)
            navController.graph = navGraph
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_all_guides,
                    R.id.nav_my_guides,
                    R.id.nav_manage_users,
                    R.id.nav_pending_new_guides,
                    R.id.nav_reported_guides
                ), binding.drawerLayout
            )
        } else if (user.userType == User.Type.NORMAL) {
            Log.i(TAG, "User is normal-user")
            binding.navView.inflateMenu(R.menu.activity_dashboard_drawer_normal)
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_normal)
            navController.graph = navGraph
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_all_guides,
                    R.id.nav_my_guides
                ), binding.drawerLayout
            )
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun resetUI() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(USER_EXTRA_KEY, user)
        startActivity(intent)
        finish()
    }
}