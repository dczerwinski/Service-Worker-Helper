package com.wat.serviceworkerhelper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.utils.DatabaseUtils
import com.wat.serviceworkerhelper.utils.HashMapKeys
import com.wat.serviceworkerhelper.utils.HashMapKeys.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlin.collections.set

class GuidesViewModel(
    private val repository: GuideEntityRepository
) : ViewModel() {

    companion object {
        private const val TAG = "GuideEntityViewModel"
    }

    val allGuides = repository.allGuides.asLiveData()
    val allPendingGuides = repository.allPendingGuides.asLiveData()
    val allReportedGuides = repository.allReportedGuides.asLiveData()
    val allAddedGuides = repository.allAddedGuides.asLiveData()

    init {
        FirebaseDatabase.getInstance()
            .getReference(DatabaseUtils.GUIDES.value)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    Log.d(TAG, "onChildAdded")
                    val key = snapshot.key!!
                    val guide = snapshot.getValue(Guide::class.java)!!
                    guide.uid = key
                    insertFromFirebase(guide)
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    Log.d(TAG, "onChildChanged")
                    val key = snapshot.key!!
                    val guide = snapshot.getValue(Guide::class.java)!!
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

    private fun insertFromFirebase(guide: Guide) = viewModelScope.launch {
        Log.d(TAG, "insert $guide")
        repository.insertFromFirebase(guide)
    }

    fun insert(guide: Guide) = viewModelScope.launch {
        Log.d(TAG, "insert $guide")
        repository.insert(guide)
    }

    private fun deleteFromFirebase(uid: String) = viewModelScope.launch {
        repository.deleteFromFirebase(uid)
    }

    fun delete(uid: String) = viewModelScope.launch {
        repository.delete(uid)
    }

    private fun updateFromFirebase(guide: Guide) = viewModelScope.launch {
        repository.updateFromFirebase(guide)
    }

    fun update(guide: Guide) = viewModelScope.launch {
        repository.update(guide)
    }

    fun changeOpinion(guide: Guide) = viewModelScope.launch {
        var temp = 0F
        val opinionsStats = hashMapOf(
            Pair(OPINION_STATS_5.value, 0),
            Pair(OPINION_STATS_4.value, 0),
            Pair(OPINION_STATS_3.value, 0),
            Pair(OPINION_STATS_2.value, 0),
            Pair(OPINION_STATS_1.value, 0)
        )

        for (opinion in guide.opinions.values) {
            val rate = opinion.rate
            temp += rate
            opinionsStats[HashMapKeys.getKey(rate.toInt())] =
                opinionsStats[HashMapKeys.getKey(rate.toInt())]!!.plus(1)
        }

        guide.opinionsStats = opinionsStats
        if (temp != 0F && guide.opinions.values.isNotEmpty()) {
            guide.rate = temp / guide.opinions.values.size
        } else if (temp == 0F && guide.opinions.values.isNotEmpty()) {
            guide.rate = 0F
        } else {
            guide.rate = -1F
        }

        repository.update(guide)
    }

    class GuidesViewModelFactory(
        private val repository: GuideEntityRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GuidesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")//TODO
                return GuidesViewModel(repository) as T
            }
            throw IllegalAccessException("Unknown ViewModel class")
        }
    }
}