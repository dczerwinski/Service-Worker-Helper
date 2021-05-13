package com.wat.serviceworkerhelper.model.repositories

import androidx.annotation.WorkerThread
import com.wat.serviceworkerhelper.model.daos.GuideEntityDao
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.utils.DatabaseUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow

class GuideEntityRepository(
    private val dao: GuideEntityDao
) {

    val allGuides: Flow<List<Guide>> = dao.getAllGuides()
    val allPendingGuides: Flow<List<Guide>> = dao.getAllGuidesByStatus(Guide.Status.PENDING)
    val allReportedGuides: Flow<List<Guide>> = dao.getAllGuidesByStatus(Guide.Status.REPORTED)
    val allAddedGuides: Flow<List<Guide>> = dao.getAllGuidesByStatus(Guide.Status.ADDED)

    @WorkerThread
    suspend fun insertFromFirebase(guide: Guide) {
        dao.insert(guide)
    }

    @WorkerThread
    suspend fun insert(guide: Guide) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.GUIDES.value)
            .child(guide.uid)
            .setValue(guide)
        dao.insert(guide)
    }

    @WorkerThread
    suspend fun deleteFromFirebase(uid: String) {
        dao.delete(uid)
    }

    @WorkerThread
    suspend fun delete(uid: String) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.GUIDES.value)
            .child(uid)
            .removeValue()
        dao.delete(uid)
    }

    @WorkerThread
    suspend fun updateFromFirebase(guide: Guide) {
        dao.update(guide)
    }

    @WorkerThread
    suspend fun update(guide: Guide) {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.GUIDES.value)
            .child(guide.uid)
            .setValue(guide)
        dao.update(guide)
    }
}