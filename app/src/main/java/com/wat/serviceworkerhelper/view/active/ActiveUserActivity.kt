package com.wat.serviceworkerhelper.view.active

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.ActivityActiveUserBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.viewmodel.UsersViewModel

class ActiveUserActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ActiveUserActivity"
    }

    private val auth = FirebaseAuth.getInstance()
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private val userRepository by lazy { UserEntityRepository(database.userDao()) }
    private val usersViewModel: UsersViewModel by viewModels {
        UsersViewModel.UsersViewModelFactory(userRepository)
    }
    private var usersList = ArrayList<User>()
    private lateinit var binding: ActivityActiveUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usersViewModel.allUsers.observe(this, {
            usersList = ArrayList(it)
        })

        binding.activeUpButton.setOnClickListener {
            val password = binding.passwordTV.text.toString()
            val reEnterPassword = binding.reEnterPasswordTV.text.toString()
            val email = binding.emailTV.text.toString()
            if (
                password.isNotEmpty() &&
                reEnterPassword.isNotEmpty() &&
                password == reEnterPassword
            ) {
                if (binding.displayNameTV.text.length < 6) {
                    onFinish(Result.WRONG_DISPLAY_NAME)
                } else {
                    val displayName = binding.displayNameTV.text.toString()
                    val user = findUser(email)
                    if (user != null) {
                        startActiveUser(user, password, displayName)
                    } else {
                        onFinish(Result.USER_NOT_FOUND)
                    }
                }
            } else {
                onFinish(Result.DIFFERENT_PASSWORDS)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun findUser(email: String): User? {
        for (user in usersList) {
            if (user.email == email) return user
        }
        return null
    }

    private fun startActiveUser(user: User, password: String, displayName: String) {
        auth
            .createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener {
                val u = auth.currentUser!!
                val uri = Uri.parse(user.photoURL)
                val profileUpdates = UserProfileChangeRequest.Builder().apply {
                    this.displayName = displayName
                    photoUri = uri
                }.build()
                usersViewModel.delete(user.uid)
                user.apply {
                    uid = u.uid
                    isActivated = true
                    this.displayName = displayName
                }
                usersViewModel.insert(user)
                u.updateProfile(profileUpdates)
                u.sendEmailVerification()
                auth.currentUser!!.sendEmailVerification()
                auth.signOut()
                Log.i(TAG, "startActiveUser user activated!")
                onFinish(Result.SUCCESS)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "createUserWithEmail:failure", e)
                if (e is FirebaseAuthWeakPasswordException) {
                    onFinish(Result.WEAK_PASSWORD)
                } else {
                    onFinish(Result.AUTH_FAIL)
                }
            }
    }

    private fun onFinish(result: Result) {
        Log.i(TAG, "onFinish result = $result")
        when (result) {
            Result.SUCCESS -> {
                Toast.makeText(
                    this,
                    getString(R.string.success_user_activation),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }

            Result.WEAK_PASSWORD -> {
                Toast.makeText(
                    this,
                    getString(R.string.too_weak_password),
                    Toast.LENGTH_SHORT
                ).show()
            }

            Result.AUTH_FAIL -> {
                Toast.makeText(
                    this,
                    R.string.authentication_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }

            Result.DIFFERENT_PASSWORDS -> {
                Toast.makeText(
                    this,
                    getString(R.string.different_passwords),
                    Toast.LENGTH_SHORT
                ).show()
            }

            Result.USER_NOT_FOUND -> {
                Toast.makeText(
                    this,
                    getString(R.string.user_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }

            Result.WRONG_DISPLAY_NAME -> {
                Toast.makeText(
                    this,
                    getString(R.string.wrong_display_name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private enum class Result {
        SUCCESS,
        WEAK_PASSWORD,
        AUTH_FAIL,
        DIFFERENT_PASSWORDS,
        USER_NOT_FOUND,
        WRONG_DISPLAY_NAME
    }
}