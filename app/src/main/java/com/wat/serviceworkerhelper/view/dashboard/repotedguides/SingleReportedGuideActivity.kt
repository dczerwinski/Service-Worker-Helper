package com.wat.serviceworkerhelper.view.dashboard.repotedguides

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ActivitySingleReportedGuideBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.model.repositories.ReportEntityRepository
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.wat.serviceworkerhelper.view.dialogs.LoadingDialog
import com.wat.serviceworkerhelper.view.steps.StepsRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.steps.StepsUpdater
import com.wat.serviceworkerhelper.view.tags.OnTagAddListener
import com.wat.serviceworkerhelper.view.tags.TagsRecyclerViewAdapter
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import com.wat.serviceworkerhelper.viewmodel.ReportsViewModel

class SingleReportedGuideActivity :
    AppCompatActivity(), OnTagAddListener, StepsUpdater.OnUploadEndListener {

    companion object {
        private const val PICK_IMAGE_CODE = 61
        const val GUIDE_KEY = "Somekey"
    }

    private lateinit var binding: ActivitySingleReportedGuideBinding
    private lateinit var guide: Guide
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var tagsAdapter: TagsRecyclerViewAdapter
    private lateinit var stepsAdapter: StepsRecyclerViewAdapter
    private lateinit var tagsViewManager: StaggeredGridLayoutManager
    private lateinit var stepsViewManager: GridLayoutManager
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val reportsRepository by lazy { ReportEntityRepository(database.reportsDao()) }
    private val guidesRepository by lazy { GuideEntityRepository(database.guideDao()) }
    private val reportsViewModel: ReportsViewModel by viewModels {
        ReportsViewModel.ReportsViewModelFactory(reportsRepository)
    }
    private val guidesViewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(guidesRepository)
    }
    private val loadingDialog by lazy {
        LoadingDialog(this, R.style.LoadingDialog, getString(R.string.adding_guide))
    }
    private val onContentLongClickListener = View.OnLongClickListener {
        Toast.makeText(this, R.string.guide_title, Toast.LENGTH_LONG).show()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleReportedGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        guide = intent.extras!!.get(GUIDE_KEY) as Guide

        observeViewModels()
        setUpTags()
        setUpSteps()
        setUpUI(guide)
        setUpButtons()
        setUpLayoutsLogListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onTagAdd() {
        tagsViewManager.spanCount = (tagsAdapter.itemCount / 4) + 1
    }

    override fun onUploadEnd(guide: Guide) {
        guidesViewModel.update(guide)
        reportsViewModel.delete(guide.uid)
        loadingDialog.dismiss()
        Toast.makeText(this, R.string.guide_corrected, Toast.LENGTH_SHORT).show()
        finish()
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

    private fun setUpTags() {
        tagsViewManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        tagsAdapter = TagsRecyclerViewAdapter(true, this)
        tagsRecyclerView = findViewById<RecyclerView>(R.id.tagsRecyclerView).apply {
            setHasFixedSize(true)
            adapter = tagsAdapter
            layoutManager = tagsViewManager
            addItemDecoration(ItemDecoration())
        }
    }

    private fun setUpSteps() {
        stepsViewManager = GridLayoutManager(this, 1)
        stepsAdapter = StepsRecyclerViewAdapter(this, onContentLongClickListener)
        stepsRecyclerView = findViewById<RecyclerView>(R.id.stepsRecyclerView).apply {
            setHasFixedSize(true)
            adapter = stepsAdapter
            layoutManager = stepsViewManager
            addItemDecoration(ItemDecoration())
        }
    }

    private fun setUpUI(guide: Guide) {
        binding.toolbarLayout.title = guide.title
        binding.content.guideNameEditText.setText(guide.title)
        tagsAdapter.setList(guide.tags)
        stepsAdapter.setList(guide.steps, PICK_IMAGE_CODE)
        tagsViewManager.spanCount = (guide.tags.size / 4) + 1
        tagsAdapter.setList(guide.tags)
    }

    private fun observeViewModels() {
        reportsViewModel.allReports.observe(this, {
            it.forEach { report ->
                if (report.guideUID == guide.uid) {
                    binding.content.reportDescription.text = report.description
                }
            }
        })

        guidesViewModel.allReportedGuides.observe(this, {
            it.forEach { g ->
                if (g.uid == guide.uid) {
                    guide = g
                    setUpUI(g)
                }
            }
        })
    }

    private fun setUpButtons() {
        binding.content.declineButton.setOnClickListener {
            StepsUpdater.delete(guide.uid, guide.steps.size)
            guidesViewModel.delete(guide.uid)
            reportsViewModel.delete(guide.uid)
            Toast.makeText(this, R.string.guide_deleted, Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.content.acceptButton.setOnClickListener {
            loadingDialog.show()
            val g = Guide(
                guide.uid,
                binding.content.guideNameEditText.text.toString(),
                ArrayList(),
                tagsAdapter.getTags(),
                guide.opinions,
                guide.rate,
                guide.opinionsStats,
                guide.creatorUID,
                guide.creationDate
            )

            StepsUpdater(this).start(stepsAdapter.getItems(), g)
        }
    }

    private fun setUpLayoutsLogListeners() {
        binding.content.layoutGuideTitle.setOnLongClickListener(onContentLongClickListener)
        binding.content.layoutGuideContent.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_content, Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
        binding.content.layoutGuideTags.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_tags, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        binding.content.layoutGuideReport.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_report, Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
    }
}