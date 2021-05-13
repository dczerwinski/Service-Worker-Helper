package com.wat.serviceworkerhelper.model.repositories

import androidx.annotation.WorkerThread
import com.wat.serviceworkerhelper.model.daos.ReportEntityDao
import com.wat.serviceworkerhelper.model.entities.Report
import com.wat.serviceworkerhelper.utils.DatabaseUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow

class ReportEntityRepository(
    private val dao: ReportEntityDao
) {

    val allReports: Flow<List<Report>> = dao.getAllReports()

    @WorkerThread
    suspend fun insertFromFirebase(report: Report) {
        dao.insert(report)
    }

    @WorkerThread
    suspend fun insert(report: Report) {
        FirebaseDatabase
            .getInstance()
            .getReference(DatabaseUtils.REPORTS.value)
            .child(report.guideUID)
            .setValue(report)
        dao.insert(report)
    }

    @WorkerThread
    suspend fun deleteFromFirebase(guideUID: String) {
        dao.delete(guideUID)
    }

    @WorkerThread
    suspend fun delete(guideUID: String) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.REPORTS.value)
            .child(guideUID)
            .removeValue()
        dao.delete(guideUID)
    }

    @WorkerThread
    suspend fun updateFromFirebase(report: Report) {
        dao.update(report)
    }

    @WorkerThread
    suspend fun update(report: Report) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.REPORTS.value)
            .child(report.creatorUID)
            .setValue(report)
        dao.update(report)
    }
}