package com.wat.serviceworkerhelper.model.daos

import androidx.room.*
import com.wat.serviceworkerhelper.model.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserEntityDao {

    @Query("SELECT * FROM users_table ORDER BY displayName ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users_table WHERE uid LIKE :userUID ORDER BY displayName ASC")
    fun getUserByUID(userUID: String): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Update(entity = User::class)
    suspend fun update(user: User)

    @Query("DELETE FROM users_table")
    suspend fun deleteAll()

    @Query("DELETE FROM users_table WHERE uid LIKE :uid")
    suspend fun delete(uid: String)
}