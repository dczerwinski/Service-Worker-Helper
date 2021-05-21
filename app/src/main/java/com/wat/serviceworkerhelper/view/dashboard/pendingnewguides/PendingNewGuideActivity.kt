package com.wat.serviceworkerhelper.view.dashboard.pendingnewguides

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ActivityPendingNewGuideBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.wat.serviceworkerhelper.utils.TextChangeListener
import com.wat.serviceworkerhelper.view.dialogs.LoadingDialog
import com.wat.serviceworkerhelper.view.steps.StepsRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.steps.StepsUpdater
import com.wat.serviceworkerhelper.view.tags.OnTagAddListener
import com.wat.serviceworkerhelper.view.tags.TagAddListener
import com.wat.serviceworkerhelper.view.tags.TagsRecyclerViewAdapter
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel

class PendingNewGuideActivity :
    AppCompatActivity(), OnTagAddListener, StepsUpdater.OnUploadEndListener {

    companion object {
        private const val PICK_IMAGE_CODE = 68
        private const val TAG = "PendingNewGuideActivity"
        const val GUIDE_KEY = "GUIDE_KEY"
    }

    private lateinit var binding: ActivityPendingNewGuideBinding
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var tagsAdapter: TagsRecyclerViewAdapter
    private lateinit var stepsAdapter: StepsRecyclerViewAdapter
    private lateinit var tagsViewManager: StaggeredGridLayoutManager
    private lateinit var stepsViewManager: GridLayoutManager
    private val titleLiveData = MutableLiveData<String>()
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val repository by lazy { GuideEntityRepository(database.guideDao()) }
    private val viewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(repository)
    }
    private val loadingDialog by lazy {
        LoadingDialog(this, R.style.LoadingDialog, getString(R.string.adding_guide))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingNewGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val guide = intent.extras!!.get(GUIDE_KEY) as Guide

        tagsViewManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        stepsViewManager = GridLayoutManager(this, 1)
        tagsAdapter = TagsRecyclerViewAdapter(true, this)
        stepsAdapter = StepsRecyclerViewAdapter(this)

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

        titleLiveData.postValue(guide.title)
        titleLiveData.observe(this, {
            binding.toolbarLayout.title = it
        })

        binding.content.guideNameEditText.setText(guide.title)
        binding.content.guideNameEditText.addTextChangedListener(object : TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                titleLiveData.postValue(s.toString())
            }
        })

        tagsViewManager.spanCount = (guide.tags.size / 4) + 1
        tagsAdapter.setList(guide.tags)
        stepsAdapter.setList(guide.steps, PICK_IMAGE_CODE)
        binding.content.tagsEditText.addTextChangedListener(
            TagAddListener(tagsAdapter, binding.content.tagsEditText, this)
        )

        setUpLayoutsLogListeners()

        binding.content.acceptButton.setOnClickListener {
            loadingDialog.show()
            guide.title = binding.content.guideNameEditText.text.toString()
            guide.tags = tagsAdapter.getTags()
            guide.guideStatus = Guide.Status.ADDED
            StepsUpdater(this).start(stepsAdapter.getItems(), guide)
        }
    }

    override fun onUploadEnd(guide: Guide) {
        loadingDialog.dismiss()
        viewModel.update(guide)
        Toast.makeText(
            this,
            getString(R.string.success_adding_guide),
            Toast.LENGTH_SHORT
        ).show()
        Log.i(TAG, "Success adding pending guide!")
        finish()
    }

    override fun onTagAdd() {
        tagsViewManager.spanCount = (tagsAdapter.itemCount / 4) + 1
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    private fun setUpLayoutsLogListeners() {
        binding.content.layoutGuideTitle.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_title, Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
        binding.content.layoutGuideContent.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_content, Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
        binding.content.layoutGuideTags.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_tags, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }
}