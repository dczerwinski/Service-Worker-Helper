package com.wat.serviceworkerhelper.model.repositories

import androidx.annotation.WorkerThread
import com.wat.serviceworkerhelper.model.daos.UserEntityDao
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.utils.DatabaseUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow

class UserEntityRepository(
    private val dao: UserEntityDao
) {

    val allUsers: Flow<List<User>> = dao.getAllUsers()
    fun currentUser(currentUserUID: String): Flow<List<User>> = dao.getUserByUID(currentUserUID)

    @WorkerThread
    suspend fun insertFromFirebase(user: User) {
        dao.insert(user)
    }

    @WorkerThread
    suspend fun insert(user: User) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.USERS.value)
            .child(user.uid)
            .setValue(user)
        dao.insert(user)
    }

    @WorkerThread
    suspend fun deleteFromFirebase(uid: String) {
        dao.delete(uid)
    }

    @WorkerThread
    suspend fun delete(uid: String) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.USERS.value)
            .child(uid)
            .removeValue()
        dao.delete(uid)
    }

    @WorkerThread
    suspend fun updateFromFirebase(user: User) {
        dao.update(user)
    }

    @WorkerThread
    suspend fun update(user: User) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.USERS.value)
            .child(user.uid)
            .setValue(user)
        dao.update(user)
    }
}