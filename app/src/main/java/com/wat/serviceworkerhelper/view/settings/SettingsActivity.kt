package com.wat.serviceworkerhelper.view.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel
import com.wat.serviceworkerhelper.view.dashboard.manageusers.UserActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_CODE = 71
        private const val TAG = "SettingsActivity"
    }

    private lateinit var user: User
    private lateinit var originalUser: User
    private var fileUri: Uri? = null
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        user = intent.extras!![USER_KEY] as User
        originalUser = user

        setUpUI(user)

        usersViewModel.allUsers.observe(this, { users ->
            for (user in users) {
                if (user.uid == FirebaseAuth.getInstance().currentUser!!.uid) {
                    if (user != this@SettingsActivity.user) {
                        this@SettingsActivity.user = user
                        setUpUI(user)
                    }
                }
            }
        })

        chooseButton.setOnClickListener {
            choosePhoto()
        }

        saveButton.setOnClickListener {
            save()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (
            requestCode == PICK_IMAGE_CODE &&
            resultCode == RESULT_OK &&
            data != null &&
            data.data != null
        ) {
            fileUri = data.data
            Picasso.get().load(fileUri).into(avatar)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpUI(user: User) {
        Picasso.get().load(user.photoURL).into(avatar)
        displayName.setText(user.displayName)
        email.setText(user.email)
    }

    private fun choosePhoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_avatar)),
            PICK_IMAGE_CODE
        )
    }

    private fun save() {
        val saveController = SaveController(usersViewModel, this, this.user)
        val newEmail = email.text.toString()
        val newDisplayName = displayName.text.toString()
        if (fileUri != null) {
            uploadImage(saveController)
        }

        if (newDisplayName != user.displayName) {
            saveController.startNewTask(SaveController.Task.CHANGE_DISPLAY_NAME)
            saveController.user.displayName = newDisplayName
            saveController.addSuccess(SaveController.Task.CHANGE_DISPLAY_NAME)
        }

        if (newEmail != user.email) {
            saveController.startNewTask(SaveController.Task.CHANGE_EMAIL)
            if (isEmailValid(newEmail)) {
                saveController.user.email = newEmail
                saveController.addSuccess(SaveController.Task.CHANGE_EMAIL)
            } else {
                saveController.addFail(
                    SaveController.Task.CHANGE_EMAIL,
                    getString(R.string.email_is_not_valid)
                )
            }
        }
    }

    private fun uploadImage(saveController: SaveController) {
        saveController.startNewTask(SaveController.Task.CHANGE_PHOTO)
        FirebaseStorage
            .getInstance()
            .reference
            .child("images/" + saveController.user.uid)
            .putFile(fileUri!!)
            .addOnSuccessListener {
                FirebaseStorage
                    .getInstance()
                    .reference
                    .child("images/" + saveController.user.uid)
                    .downloadUrl
                    .addOnSuccessListener {
                        saveController.user.photoURL = it.toString()
                        saveController.addSuccess(SaveController.Task.CHANGE_PHOTO)
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Fail during getting new photo uri!", it)
                        saveController.addFail(
                            SaveController.Task.CHANGE_PHOTO,
                            getString(R.string.upload_foto_fail)
                        )
                    }
            }
            .addOnFailureListener {
                Log.e(TAG, "Fail during uploading photo!", it)
                saveController.addFail(
                    SaveController.Task.CHANGE_PHOTO,
                    getString(R.string.upload_foto_fail)
                )
            }
    }

    private fun isEmailValid(email: String): Boolean =
        !(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())

    private class SaveController(
        private val usersViewModel: UsersViewModel,
        private val activity: Activity,
        val user: User
    ) {

        private val taskList = HashMap<Task, State>()
        private val failList = ArrayList<String>()

        fun startNewTask(task: Task) {
            taskList[task] = State.IN_PROGRESS
        }

        fun addSuccess(task: Task) {
            taskList[task] = State.SUCCESS
            checkState()
        }

        fun addFail(task: Task, fail: String) {
            taskList[task] = State.FAIL
            failList.add(fail)
            checkState()
        }

        private fun isEnd(): Boolean {
            taskList.values.forEach {
                if (it == State.IN_PROGRESS) {
                    return false
                }
            }
            return true
        }

        private fun checkState() {
            if (isEnd()) {
                if (failList.isNotEmpty()) {
                    Log.e(TAG, "task list error = $taskList")
                    Toast.makeText(activity, failList[0], Toast.LENGTH_SHORT).show()
                } else {
                    val profileUpdates = UserProfileChangeRequest.Builder().apply {
                        photoUri = Uri.parse(user.photoURL)
                        displayName = user.displayName
                    }.build()
                    FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
                    usersViewModel.update(user)
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.settings_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    activity.finish()
                }
            }
        }

        enum class Task {
            CHANGE_PHOTO,
            CHANGE_EMAIL,
            CHANGE_DISPLAY_NAME
        }

        enum class State {
            IN_PROGRESS,
            SUCCESS,
            FAIL
        }
    }
}