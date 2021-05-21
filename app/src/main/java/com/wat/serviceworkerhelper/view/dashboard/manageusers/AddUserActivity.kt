package com.wat.serviceworkerhelper.view.dashboard.manageusers

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ActivityAddUserBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.utils.TextChangeListener
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel
import java.util.*

class AddUserActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddUserActivity"
    }

    private lateinit var binding: ActivityAddUserBinding
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbarLayout.title = getString(R.string.add_user)

        val spinnerItems = arrayOf(getString(R.string.normal), "Admin")
        val arrayAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems)
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        binding.content.userTypeSpinner.adapter = arrayAdapter

        binding.content.emailEditText.addTextChangedListener(object : TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isEmailValid(s.toString())) {
                    binding.content.emailEditText.setTextColor(Color.RED)
                } else {
                    binding.content.emailEditText.setTextColor(Color.BLACK)
                }
            }
        })

        binding.content.addUserButton.setOnClickListener {
            val email = binding.content.emailEditText.text.toString()
            if (isEmailValid(email)) {
                val userType = if (binding.content.userTypeSpinner.selectedItemId == 0L) {
                    User.Type.NORMAL
                } else {
                    User.Type.ADMIN
                }
                usersViewModel.insert(
                    User(
                        UUID.randomUUID().toString(),
                        email = email,
                        photoURL = getString(R.string.defaultAvatarUrl),
                        userType = userType,
                    )
                )
                Toast.makeText(
                    this,
                    R.string.user_created,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(this, R.string.email_is_not_valid, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun isEmailValid(email: String) =
        !(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
}