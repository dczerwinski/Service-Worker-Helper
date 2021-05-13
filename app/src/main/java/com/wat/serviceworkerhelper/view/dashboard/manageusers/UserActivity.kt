package com.wat.serviceworkerhelper.view.dashboard.manageusers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel
import com.squareup.picasso.Picasso
import com.wat.serviceworkerhelper.view.dialogs.ReportDialog
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_user.*

class UserActivity : AppCompatActivity() {

    companion object {
        const val USER_KEY = "USER_KEY"
    }

    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        user = intent.extras!!.get(USER_KEY) as User
        toolbar_layout.title = user.displayName
        displayName.text = user.displayName
        email.text = user.email
        val spinnerItems = arrayOf(getString(R.string.normal), "Admin")
        val arrayAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems)
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        userTypeSpinner.adapter = arrayAdapter
        if (user.userType == User.Type.ADMIN) {
            userTypeSpinner.setSelection(1)
        } else {
            userTypeSpinner.setSelection(0)
        }
        Picasso.get().load(user.photoURL).into(avatar)

        saveButton.setOnClickListener {
            user.userType = User.Type.toType(userTypeSpinner.selectedItem.toString(), this)
            usersViewModel.update(user)
            Toast.makeText(this, R.string.user_changed, Toast.LENGTH_SHORT).show()
            finish()
        }

        sendEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${user.email}")
            intent.putExtra(Intent.EXTRA_EMAIL, user.email)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteItem -> {
                val builder =
                    AlertDialog.Builder(this, R.style.alertDialog)
                builder.setMessage(getString(R.string.are_you_sure))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        usersViewModel.delete(user.uid)
                        Toast.makeText(this, R.string.user_deleted, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}