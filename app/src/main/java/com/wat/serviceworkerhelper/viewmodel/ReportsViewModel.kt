package com.wat.serviceworkerhelper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wat.serviceworkerhelper.model.entities.Report
import com.wat.serviceworkerhelper.model.repositories.ReportEntityRepository
import com.wat.serviceworkerhelper.utils.DatabaseUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val repository: ReportEntityRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ReportsViewModel"
    }

    val allReports = repository.allReports.asLiveData()

    init {
        FirebaseDatabase
            .getInstance()
            .getReference(DatabaseUtils.REPORTS.value)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildAdded $snapshot")
                    val report = snapshot.getValue(Report::class.java)!!
                    Log.d(TAG, "report = $report")
                    insertFromFirebase(report)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildChanged")
                    val report = snapshot.getValue(Report::class.java)!!
                    updateFromFirebase(report)
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

    private fun insertFromFirebase(report: Report) = viewModelScope.launch {
        repository.insertFromFirebase(report)
    }

    fun insert(report: Report) = viewModelScope.launch {
        Log.d(TAG, "insert $report")
        repository.insert(report)
    }

    private fun deleteFromFirebase(guideUID: String) = viewModelScope.launch {
        repository.deleteFromFirebase(guideUID)
    }

    fun delete(guideUID: String) = viewModelScope.launch {
        repository.delete(guideUID)
    }

    private fun updateFromFirebase(report: Report) = viewModelScope.launch {
        repository.updateFromFirebase(report)
    }

    fun update(report: Report) = viewModelScope.launch {
        repository.update(report)
    }

    class ReportsViewModelFactory(
        private val repository: ReportEntityRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") //TODO
                return ReportsViewModel(repository) as T
            }
            throw IllegalAccessException("Unknown ViewModel class")
        }
    }
}