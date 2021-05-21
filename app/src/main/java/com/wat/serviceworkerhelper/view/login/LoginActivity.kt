package com.wat.serviceworkerhelper.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ActivityLoginBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.view.active.ActiveUserActivity
import com.wat.serviceworkerhelper.view.dashboard.DashboardActivity
import com.wat.serviceworkerhelper.view.dialogs.EmailNotVerifiedDialog
import com.wat.serviceworkerhelper.view.dialogs.LoadingDialog
import com.wat.serviceworkerhelper.view.dialogs.ResetPasswordDialog
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel

class LoginActivity : AppCompatActivity() {

    companion object {
        const val USER_EXTRA_KEY = "USER_EXTRA_KEY"
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val loadingDialog by lazy { LoadingDialog(this, R.style.LoadingDialog) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }
    private var allUsers = ArrayList<User>()
    private var isSaved = false

    @Volatile
    private var isAlreadyLogged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        usersViewModel.allUsers.observe(this, { users ->
            allUsers = ArrayList(users)
            if (isSaved) {
                getUserInfo()
            }
        })

        binding.loginButton.setOnClickListener {
            loadingDialog.show()
            val email = binding.emailTV.text.toString()
            val password = binding.passwordTV.text.toString()
            when {
                email.isEmpty() -> {
                    loadingDialog.dismiss()
                    Toast.makeText(this, getString(R.string.empty_email), Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    loadingDialog.dismiss()
                    Toast.makeText(this, getString(R.string.empty_password), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    val user = getUserByEmail(email)
                    if (user == null) {
                        loadingDialog.dismiss()
                        Log.w(TAG, "user not found")
                        Toast.makeText(
                            this,
                            R.string.user_not_found,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!user.isActivated) {
                        loadingDialog.dismiss()
                        Log.w(TAG, "not activated")
                        Toast.makeText(
                            this, R.string.user_not_activated,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        auth
                            .signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                verifyUser(user)
                            }
                            .addOnFailureListener {
                                loadingDialog.dismiss()
                                Log.w(TAG, "signInWithEmail:failure", it)
                                Toast.makeText(
                                    this, R.string.authentication_failed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
        }

        binding.activeButton.setOnClickListener {
            val intent = Intent(this, ActiveUserActivity::class.java)
            startActivity(intent)
        }

        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)
        resetPasswordButton.setOnClickListener {
            ResetPasswordDialog().show(supportFragmentManager, "ResetPasswordDialog")
        }
    }

    override fun onStart() {
        super.onStart()
        verifyUser()
    }

    override fun onDestroy() {
        loadingDialog.dismiss()
        super.onDestroy()
    }

    private fun verifyUser(user: User? = null) {
        val currentUser = auth.currentUser
        Log.d(TAG, "current user = $currentUser")
        if (currentUser != null) {
            isSaved = true
            loadingDialog.show()
            if (currentUser.isEmailVerified) {
                getUserInfo(user)
            } else {
                loadingDialog.dismiss()
                auth.signOut()
                EmailNotVerifiedDialog(currentUser)
                    .show(supportFragmentManager, "EmailNotVerifiedDialog")
            }
        }
    }

    private fun getUserByEmail(email: String): User? {
        for (user in allUsers) {
            if (user.email == email) return user
        }
        return null
    }

    private fun getUserInfo(user: User? = null) {
        if (!isAlreadyLogged) {
            if (user != null) {
                isAlreadyLogged = true
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra(USER_EXTRA_KEY, user)
                startActivity(intent)
                finish()
            } else if (allUsers.isNotEmpty() && auth.currentUser != null) {
                Log.i(TAG, "getUserInfo  user is null")
                var found = false
                for (usr in allUsers) {
                    if (auth.currentUser != null) {
                        if (usr.isActivated) {
                            isAlreadyLogged = true
                            found = true
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra(USER_EXTRA_KEY, usr)
                            startActivity(intent)
                            finish()
                        } else {
                            loadingDialog.dismiss()
                            Log.i(TAG, "user not activated")
                            Toast.makeText(
                                this, R.string.user_not_activated,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                if (!found) {
                    loadingDialog.dismiss()
                    Log.i(TAG, "fail during getting user info!")
                    Toast.makeText(
                        this, R.string.authentication_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}