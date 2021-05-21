package com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ActivitySingleGuideBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.utils.HashMapKeys.*
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.wat.serviceworkerhelper.view.dialogs.ExportGuideDialog
import com.wat.serviceworkerhelper.view.dialogs.RateDialog
import com.wat.serviceworkerhelper.view.dialogs.ReportDialog
import com.wat.serviceworkerhelper.view.opinions.OpinionsRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.steps.StepsRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.tags.TagsRecyclerViewAdapter
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel

class SingleGuideActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SingleGuideActivity"
        const val GUIDE_KEY = "GUIDE_KEY"
    }

    private lateinit var binding: ActivitySingleGuideBinding
    private lateinit var opinionsRecyclerView: RecyclerView
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var opinionsAdapter: OpinionsRecyclerViewAdapter
    private lateinit var tagsAdapter: TagsRecyclerViewAdapter
    private lateinit var stepsAdapter: StepsRecyclerViewAdapter
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var tagsViewManager: StaggeredGridLayoutManager
    private lateinit var guide: Guide

    private var myRate: Float = -1F
    private var usersList = ArrayList<User>()
    private var isFavourite = false
    private var currentUser: User? = null
    private var menu: Menu? = null

    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val repository by lazy { GuideEntityRepository(database.guideDao()) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val guidesViewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(repository)
    }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.content.contentMyRate.layoutOpinionAdded.visibility = View.GONE

        guide = intent.extras!!.get(GUIDE_KEY) as Guide

        viewManager = LinearLayoutManager(this)
        tagsViewManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        opinionsAdapter = OpinionsRecyclerViewAdapter(this)
        tagsAdapter = TagsRecyclerViewAdapter(false, null)
        stepsAdapter = StepsRecyclerViewAdapter(null)

        usersViewModel.allUsers.observe(this, {
            opinionsAdapter.setUsersList(ArrayList(it))
            usersList = ArrayList(it)
            for (user in usersList) {
                if (user.uid == FirebaseAuth.getInstance().currentUser!!.uid) {
                    currentUser = user
                    break
                }
            }
            setUpUI(guide)
        })

        opinionsRecyclerView = binding.content.opinionsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@SingleGuideActivity.opinionsAdapter
            addItemDecoration(ItemDecoration(0, 0))
        }

        tagsRecyclerView = binding.content.tagsRecyclerView.apply {
            setHasFixedSize(true)
            adapter = tagsAdapter
            layoutManager = tagsViewManager
            addItemDecoration(ItemDecoration())
        }

        stepsRecyclerView = binding.content.stepsRecyclerView.apply {
            setHasFixedSize(true)
            adapter = stepsAdapter
            layoutManager = LinearLayoutManager(this@SingleGuideActivity)
            addItemDecoration(ItemDecoration())
        }

        binding.content.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if (scrollY >= -(v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                    opinionsAdapter.addMore()
                }
            }
        )

        setUpUI(guide)
        guidesViewModel.allAddedGuides.observe(this, {
            it.forEach { arg ->
                if (arg.uid == guide.uid) {
                    guide = arg
                    setUpUI(arg)
                }
            }
        })

        binding.content.contentMyRate.editMyOpinion.setOnClickListener {
            RateDialog.newInstance(myRate, guidesViewModel, guide, false)
                .show(supportFragmentManager, "RateDialog")
        }

        binding.content.contentMyRate.myOpinionSettings.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.inflate(R.menu.menu_settings_opinion)
            popup.setOnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.action_delete -> {
                        val builder =
                            AlertDialog.Builder(this@SingleGuideActivity, R.style.alertDialog)
                        builder.setMessage(getString(R.string.are_you_sure))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                                guide.opinions.remove(uid)
                                guidesViewModel.changeOpinion(guide)
                                Toast.makeText(this, R.string.opinion_deleted, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()
                    }
                    else -> throw IllegalStateException("Wrong itemID ?")
                }
                true
            }
            popup.show()
        }

        binding.content.contentMyRate.ratingByMe.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser && rating > 0) {
                RateDialog.newInstance(rating, guidesViewModel, guide, true)
                    .show(supportFragmentManager, "RateDialog")
            }
        }

        setUpLayoutsLogListeners()
    }

    private fun setUpLayoutsLogListeners() {
        binding.content.layoutGuideContent.setOnLongClickListener {
            val toast =
                Toast.makeText(this, R.string.guide_content, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)//TODO repair this toast ? somehow
            toast.show()
            ExportGuideDialog.newInstance(guide, binding.content.layoutGuideContent, this)
                .show(supportFragmentManager, "Export to PDF")
            return@setOnLongClickListener true
        }
        binding.content.contentAuthorInfo.layoutAuthorInfo.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_author, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        binding.content.contentMyRate.layoutMyOpinion.setOnLongClickListener {
            Toast.makeText(this, R.string.rateIt, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        binding.content.contentOtherRatings.layoutRatings.setOnLongClickListener {
            Toast.makeText(this, R.string.rating, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        binding.content.layoutOpinions.setOnLongClickListener {
            Toast.makeText(this, R.string.opinions, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        binding.content.layoutGuideTags.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_tags, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUI(guide: Guide) {
        this.guide = guide
        binding.toolbarLayout.title = guide.title
        stepsAdapter.setList(guide.steps)

        if (currentUser != null && currentUser!!.categories[1].guidesUIDs.contains(guide.uid)) {
            isFavourite = true
            if (menu != null) {
                menu!!.getItem(0).icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_heart_full)
            }
        }

        tagsViewManager.spanCount = (guide.tags.size / 4) + 1
        tagsAdapter.setList(guide.tags)

        if (guide.opinions.isNotEmpty()) {
            val myOpinion: Guide.Opinion? =
                guide.opinions[FirebaseAuth.getInstance().currentUser!!.uid]
            showMyOpinion(myOpinion)
        } else {
            showRateIt()
        }

        opinionsAdapter.setList(guide.opinions)
        setUpOpinionsStats(guide.opinionsStats, guide.opinions.size)

        if (guide.rate >= 0) {
            binding.content.contentOtherRatings.rate.text = String.format("%.1f", guide.rate)
            binding.content.contentOtherRatings.ratingByOthers.rating = guide.rate
            binding.content.contentOtherRatings.ratesCount.text = guide.opinions.size.toString()
        } else {
            binding.content.contentOtherRatings.rate.text = "N/A"
            binding.content.contentOtherRatings.ratingByOthers.rating = 0F
            binding.content.contentOtherRatings.ratesCount.text = guide.opinions.size.toString()
        }

        setUpAuthorInfo(guide)
    }

    private fun setUpAuthorInfo(guide: Guide) {
        val user = getUserByUID(guide.creatorUID)
        if (user != null) {
            binding.content.contentAuthorInfo.authorDisplayName.text = user.displayName
            Picasso.get().load(user.photoURL).into(binding.content.contentAuthorInfo.authorAvatar)
        } else {
            binding.content.contentAuthorInfo.authorDisplayName.text = getString(R.string.loading)
        }
        binding.content.contentAuthorInfo.authorCreationDate.text = getString(
            R.string.creation_date,
            guide.creationDate
        )
    }

    private fun getUserByUID(uid: String): User? {
        for (user in usersList) {
            if (user.uid == uid) {
                return user
            }
        }
        return null
    }

    private fun setUpOpinionsStats(stats: HashMap<String, Int>, opinionsCount: Int) {
        Log.i(TAG, "stats = $stats, opinionsCount = $opinionsCount")
        if (opinionsCount == 0 || stats.isEmpty()) {
            binding.content.contentOtherRatings.progressBar5.progress = 0
            binding.content.contentOtherRatings.progressBar4.progress = 0
            binding.content.contentOtherRatings.progressBar3.progress = 0
            binding.content.contentOtherRatings.progressBar2.progress = 0
            binding.content.contentOtherRatings.progressBar1.progress = 0
        } else {
            binding.content.contentOtherRatings.progressBar5.progress =
                countProgress(stats[OPINION_STATS_5.value]!!, opinionsCount)
            binding.content.contentOtherRatings.progressBar4.progress =
                countProgress(stats[OPINION_STATS_4.value]!!, opinionsCount)
            binding.content.contentOtherRatings.progressBar3.progress =
                countProgress(stats[OPINION_STATS_3.value]!!, opinionsCount)
            binding.content.contentOtherRatings.progressBar2.progress =
                countProgress(stats[OPINION_STATS_2.value]!!, opinionsCount)
            binding.content.contentOtherRatings.progressBar1.progress =
                countProgress(stats[OPINION_STATS_1.value]!!, opinionsCount)
        }
    }

    private fun countProgress(arg1: Int, arg2: Int): Int {
        return (((arg1.toFloat()) / (arg2.toFloat())) * 100F).toInt()
    }

    private fun showMyOpinion(myOpinion: Guide.Opinion?) {
        if (myOpinion != null) {
            binding.content.contentMyRate.ratingByMe.visibility = View.GONE
            binding.content.contentMyRate.layoutOpinionAdded.visibility = View.VISIBLE
            binding.content.contentMyRate.myRateTitle.text = getString(R.string.yourOpinion)
            Picasso
                .get()
                .load(FirebaseAuth.getInstance().currentUser!!.photoUrl.toString())
                .into(binding.content.contentMyRate.myAvatar)
            binding.content.contentMyRate.displayName.text =
                FirebaseAuth.getInstance().currentUser!!.displayName
            binding.content.contentMyRate.myRatingBar.rating = myOpinion.rate
            myRate = myOpinion.rate
            binding.content.contentMyRate.myDate.text = myOpinion.date
            binding.content.contentMyRate.myMainOpinion.text = myOpinion.opinion
        } else {
            showRateIt()
        }
    }

    private fun showRateIt() {
        binding.content.contentMyRate.myRateTitle.text = getString(R.string.rateIt)
        binding.content.contentMyRate.ratingByMe.rating = 0F
        binding.content.contentMyRate.ratingByMe.visibility = View.VISIBLE
        binding.content.contentMyRate.layoutOpinionAdded.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_single_guide, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reportItem -> {
                ReportDialog
                    .newInstance(guide, this)
                    .show(supportFragmentManager, "Report Dialog")
                true
            }
            R.id.favorite -> {
                when (isFavourite) {
                    true -> {
                        isFavourite = false
                        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_heart_empty)
                        if (currentUser != null) {
                            currentUser!!.categories[1].guidesUIDs.remove(guide.uid)
                            usersViewModel.update(currentUser!!)
                        }
                        Toast.makeText(this, R.string.removed_from_fav, Toast.LENGTH_SHORT).show()
                    }
                    false -> {
                        isFavourite = true
                        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_heart_full)
                        if (currentUser != null) {
                            currentUser!!.categories[1].guidesUIDs.add(guide.uid)
                            usersViewModel.update(currentUser!!)
                        }
                        Toast.makeText(this, R.string.added_to_fav, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}