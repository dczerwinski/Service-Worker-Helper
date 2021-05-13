package com.wat.serviceworkerhelper.model.daos

import androidx.room.*
import com.wat.serviceworkerhelper.model.entities.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportEntityDao {

    @Query("SELECT * FROM reports_table")
    fun getAllReports(): Flow<List<Report>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: Report)

    @Update(entity = Report::class)
    suspend fun update(report: Report)

    @Query("DELETE FROM reports_table")
    suspend fun deleteAll()

    @Query("DELETE FROM reports_table WHERE guideUID LIKE :guideUID")
    suspend fun delete(guideUID: String)
}