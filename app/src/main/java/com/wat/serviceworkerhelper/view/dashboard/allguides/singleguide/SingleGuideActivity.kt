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
import com.wat.serviceworkerhelper.view.dialogs.ExportGuideDialog
import com.wat.serviceworkerhelper.view.dialogs.RateDialog
import com.wat.serviceworkerhelper.view.dialogs.ReportDialog
import com.wat.serviceworkerhelper.view.steps.StepsRecyclerViewAdapter
import com.wat.serviceworkerhelper.view.tags.TagsRecyclerViewAdapter
import com.wat.serviceworkerhelper.utils.HashMapKeys.*
import com.wat.serviceworkerhelper.utils.ItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pending_new_guide.*
import kotlinx.android.synthetic.main.content_author_info.*
import kotlinx.android.synthetic.main.content_my_rate.*
import kotlinx.android.synthetic.main.content_other_ratings.*
import kotlinx.android.synthetic.main.content_other_ratings.view.*
import kotlinx.android.synthetic.main.content_singe_guide.*

class SingleGuideActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SingleGuideActivity"
        const val GUIDE_KEY = "GUIDE_KEY"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var adapter: OpinionsRecyclerViewAdapter
    private lateinit var tagsAdapter: TagsRecyclerViewAdapter
    private lateinit var stepsAdapter: StepsRecyclerViewAdapter
    private lateinit var viewManager: GridLayoutManager
    private lateinit var tagsViewManager: StaggeredGridLayoutManager
    private lateinit var stepsViewManager: GridLayoutManager
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
        setContentView(R.layout.activity_single_guide)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        layoutOpinionAdded.visibility = View.GONE

        guide = intent.extras!!.get(GUIDE_KEY) as Guide

        viewManager = GridLayoutManager(this, 1)
        tagsViewManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        stepsViewManager = GridLayoutManager(this, 1)
        adapter = OpinionsRecyclerViewAdapter(this)
        tagsAdapter = TagsRecyclerViewAdapter(false, null)
        stepsAdapter = StepsRecyclerViewAdapter(null)

        usersViewModel.allUsers.observe(this, {
            adapter.setUsersList(ArrayList(it))
            usersList = ArrayList(it)
            for (user in usersList) {
                if (user.uid == FirebaseAuth.getInstance().currentUser!!.uid) {
                    currentUser = user
                    break
                }
            }
            setUpUI(guide)
        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = this@SingleGuideActivity.adapter
            addItemDecoration(ItemDecoration(0, 0))
        }

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

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY >= -(v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                adapter.addMore()
            }
        })

        setUpUI(guide)
        guidesViewModel.allAddedGuides.observe(this, {
            it.forEach { arg ->
                if (arg.uid == guide.uid) {
                    guide = arg
                    setUpUI(arg)
                }
            }
        })

        editMyOpinion.setOnClickListener {
            RateDialog.newInstance(myRate, guidesViewModel, guide, false)
                .show(supportFragmentManager, "RateDialog")
        }

        myOpinionSettings.setOnClickListener { view ->
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

        ratingByMe.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser && rating > 0) {
                RateDialog.newInstance(rating, guidesViewModel, guide, true)
                    .show(supportFragmentManager, "RateDialog")
            }
        }

        setUpLayoutsLogListeners()
    }

    private fun setUpLayoutsLogListeners() {
        layoutGuideContent.setOnLongClickListener {
            val toast =
                Toast.makeText(this.applicationContext, R.string.guide_content, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)//TODO repair this toast ? somehow
            toast.show()
            ExportGuideDialog.newInstance(guide, layoutGuideContent, this)
                .show(supportFragmentManager, "Export to PDF")
            return@setOnLongClickListener true
        }
        layoutAuthorInfo.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_author, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        layoutMyOpinion.setOnLongClickListener {
            Toast.makeText(this, R.string.rateIt, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        layoutRatings.setOnLongClickListener {
            Toast.makeText(this, R.string.rating, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        layoutOpinions.setOnLongClickListener {
            Toast.makeText(this, R.string.opinions, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        layoutGuideTags.setOnLongClickListener {
            Toast.makeText(this, R.string.guide_tags, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUI(guide: Guide) {
        this.guide = guide
        toolbar_layout.title = guide.title
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

        adapter.setList(guide.opinions)
        setUpOpinionsStats(guide.opinionsStats, guide.opinions.size)

        if (guide.rate >= 0) {
            rate.text = String.format("%.1f", guide.rate)
            ratingByOthers.rating = guide.rate
            ratesCount.text = guide.opinions.size.toString()
        } else {
            rate.text = "N/A"
            ratingByOthers.rating = 0F
            ratesCount.text = guide.opinions.size.toString()
        }

        setUpAuthorInfo(guide)
    }

    private fun setUpAuthorInfo(guide: Guide) {
        val user = getUserByUID(guide.creatorUID)
        if (user != null) {
            authorDisplayName.text = user.displayName
            Picasso.get().load(user.photoURL).into(authorAvatar)
        } else {
            authorDisplayName.text = getString(R.string.loading)
        }
        authorCreationDate.text = getString(R.string.creation_date, guide.creationDate)
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
            progressBar5.progress = 0
            progressBar4.progress = 0
            progressBar3.progress = 0
            progressBar2.progress = 0
            progressBar1.progress = 0
        } else {
            progressBar5.progress = countProgress(stats[OPINION_STATS_5.value]!!, opinionsCount)
            progressBar4.progress = countProgress(stats[OPINION_STATS_4.value]!!, opinionsCount)
            progressBar3.progress = countProgress(stats[OPINION_STATS_3.value]!!, opinionsCount)
            progressBar2.progress = countProgress(stats[OPINION_STATS_2.value]!!, opinionsCount)
            progressBar1.progress = countProgress(stats[OPINION_STATS_1.value]!!, opinionsCount)
        }
    }

    private fun countProgress(arg1: Int, arg2: Int): Int {
        return (((arg1.toFloat()) / (arg2.toFloat())) * 100F).toInt()
    }

    private fun showMyOpinion(myOpinion: Guide.Opinion?) {
        if (myOpinion != null) {
            ratingByMe.visibility = View.GONE
            layoutOpinionAdded.visibility = View.VISIBLE
            myRateTitle.text = getString(R.string.yourOpinion)
            Picasso
                .get()
                .load(FirebaseAuth.getInstance().currentUser!!.photoUrl.toString())
                .into(myAvatar)
            displayName.text = FirebaseAuth.getInstance().currentUser!!.displayName
            myRatingBar.rating = myOpinion.rate
            myRate = myOpinion.rate
            myDate.text = myOpinion.date
            myMainOpinion.text = myOpinion.opinion
        } else {
            showRateIt()
        }
    }

    private fun showRateIt() {
        myRateTitle.text = getString(R.string.rateIt)
        ratingByMe.rating = 0F
        ratingByMe.visibility = View.VISIBLE
        layoutOpinionAdded.visibility = View.GONE
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