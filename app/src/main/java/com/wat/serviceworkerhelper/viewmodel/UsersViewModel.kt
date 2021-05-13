package com.wat.serviceworkerhelper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.model.repositories.UserEntityRepository
import com.wat.serviceworkerhelper.utils.DatabaseUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class UsersViewModel(
    private val repository: UserEntityRepository
) : ViewModel() {

    companion object {
        private const val TAG = "UsersViewModel"
    }

    val allUsers = repository.allUsers.asLiveData()
    fun currentUser(currentUserUID: String) = repository.currentUser(currentUserUID).asLiveData()

    init {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.USERS.value)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    Log.d(TAG, "onChildAdded")
                    val key = snapshot.key!!
                    val guide = snapshot.getValue(User::class.java)!!
                    guide.uid = key
                    insertFromFirebase(guide)
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    Log.d(TAG, "onChildChanged")
                    val key = snapshot.key!!
                    val guide = snapshot.getValue(User::class.java)!!
                    guide.uid = key
                    updateFromFirebase(guide)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.d(TAG, "onChildRemoved")
                    val key = snapshot.key!!
                    deleteFromFirebase(key)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled - $error")
                }
            })
    }

    fun insertFromFirebase(user: User) = viewModelScope.launch {
        repository.insertFromFirebase(user)
    }

    fun insert(user: User) = viewModelScope.launch {
        Log.d(TAG, "insert $user")
        repository.insert(user)
    }

    fun deleteFromFirebase(uid: String) = viewModelScope.launch {
        repository.deleteFromFirebase(uid)
    }

    fun delete(uid: String) = viewModelScope.launch {
        repository.delete(uid)
    }

    private fun updateFromFirebase(user: User) = viewModelScope.launch {
        repository.updateFromFirebase(user)
    }

    fun update(user: User) = viewModelScope.launch {
        repository.update(user)
    }

    class UsersViewModelFactory(
        private val repository: UserEntityRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") //TODO
                return UsersViewModel(repository) as T
            }
            throw IllegalAccessException("Unknown ViewModel class")
        }
    }
}