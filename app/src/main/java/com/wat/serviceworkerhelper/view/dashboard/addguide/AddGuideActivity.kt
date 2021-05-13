package com.wat.serviceworkerhelper.view.dashboard.addguide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel
import com.wat.serviceworkerhelper.view.dialogs.LoadingDialog
import com.wat.serviceworkerhelper.view.steps.StepsRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.steps.StepsUpdater
import com.wat.serviceworkerhelper.view.tags.OnTagAddListener
import com.wat.serviceworkerhelper.view.tags.TagAddListener
import com.wat.serviceworkerhelper.view.tags.TagsRecyclerViewAdapter
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_add_guide.*
import kotlinx.android.synthetic.main.content_add_guide.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddGuideActivity :
    AppCompatActivity(), OnTagAddListener, StepsUpdater.OnUploadEndListener {

    companion object {
        private const val PICK_IMAGE_CODE = 69
        private const val TAG = "AddGuideActivity"
    }

    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var tagsAdapter: TagsRecyclerViewAdapter
    private lateinit var stepsAdapter: StepsRecyclerViewAdapter
    private lateinit var tagsViewManager: StaggeredGridLayoutManager
    private lateinit var stepsViewManager: GridLayoutManager
    private lateinit var currentUser: User
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val repository by lazy { GuideEntityRepository(database.guideDao()) }
    private val viewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(repository)
    }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }
    private val loadingDialog by lazy {
        LoadingDialog(this, R.style.LoadingDialog, getString(R.string.adding_guide))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_guide)
        setSupportActionBar(toolbar)

        val collapsingToolbarLayout: CollapsingToolbarLayout = findViewById(R.id.toolbar_layout)
        collapsingToolbarLayout.title = getString(R.string.add_guide)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        usersViewModel
            .currentUser(FirebaseAuth.getInstance().currentUser!!.uid)
            .observe(this, {
                currentUser = it[0]
            })

        tagsViewManager = StaggeredGridLayoutManager(
            2, StaggeredGridLayoutManager.HORIZONTAL
        )
        stepsViewManager = GridLayoutManager(this, 1)
        tagsAdapter = TagsRecyclerViewAdapter(true, this)
        stepsAdapter = StepsRecyclerViewAdapter(this)
        stepsAdapter.setList(ArrayList(), PICK_IMAGE_CODE)

        tagsRecyclerView = findViewById<RecyclerView>(R.id.tagsRecyclerView).apply {
            setHasFixedSize(true)
            adapter = tagsAdapter
            layoutManager = tagsViewManager
            addItemDecoration(ItemDecoration())
        }
        stepsRecyclerView = findViewById<RecyclerView>(R.id.stepsRecyclerView).apply {
            setHasFixedSize(true)
            adapter = stepsAdapter
            layoutManager = stepsViewManager
            addItemDecoration(ItemDecoration())
        }

        tagsEditText.addTextChangedListener(
            TagAddListener(tagsAdapter, tagsEditText, this)
        )

        addButton.setOnClickListener {
            val uid = UUID.randomUUID().toString()
            val items = stepsAdapter.getItems()
            if (guideNameEditText.text.toString().isNotEmpty() && items.isNotEmpty()) {
                loadingDialog.show()
                val newGuide = Guide(
                    uid,
                    guideNameEditText.text.toString(),
                    tags = tagsAdapter.getTags(),
                    creatorUID = FirebaseAuth.getInstance().currentUser!!.uid,
                    creationDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format((Date())),
                    guideStatus = Guide.Status.PENDING
                )
                StepsUpdater(this).start(stepsAdapter.getItems(), newGuide)
            } else {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onTagAdd() {
        tagsViewManager.spanCount = (tagsAdapter.itemCount / 4) + 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (
            requestCode == PICK_IMAGE_CODE &&
            resultCode == RESULT_OK &&
            data != null &&
            data.data != null
        ) {
            stepsAdapter.setPhotoUri(data.data!!)
        }
    }

    override fun onUploadEnd(guide: Guide) {
        viewModel.insert(guide)
        currentUser.categories[0].guidesUIDs.add(guide.uid)
        usersViewModel.update(currentUser)
        loadingDialog.dismiss()
        Toast.makeText(
            this,
            getString(R.string.success_adding_guide),
            Toast.LENGTH_SHORT
        ).show()
        Log.i(TAG, "Success adding guide!")
        finish()
    }
}