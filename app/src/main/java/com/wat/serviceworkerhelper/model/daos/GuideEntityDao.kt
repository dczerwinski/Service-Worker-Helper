package com.wat.serviceworkerhelper.model.daos

import androidx.room.*
import com.wat.serviceworkerhelper.model.entities.Guide
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideEntityDao {

    @Query("SELECT * FROM guides_table ORDER BY title ASC")
    fun getAllGuides(): Flow<List<Guide>>

    @Query("SELECT * FROM guides_table WHERE guideStatus LIKE :status ORDER BY title ASC")
    fun getAllGuidesByStatus(status: Guide.Status): Flow<List<Guide>>

    @Query("SELECT * FROM guides_table WHERE creatorUID LIKE :creatorUID ORDER BY title ASC")
    fun getAllGuidesByCreator(creatorUID: String): Flow<List<Guide>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(guide: Guide)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guides: List<Guide>)

    @Update(entity = Guide::class)
    suspend fun update(guide: Guide)

    @Query("DELETE FROM guides_table")
    suspend fun deleteAll()

    @Query("DELETE FROM guides_table WHERE uid LIKE :uid")
    suspend fun delete(uid: String)
}