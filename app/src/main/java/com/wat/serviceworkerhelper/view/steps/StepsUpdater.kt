package com.wat.serviceworkerhelper.view.steps

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.wat.serviceworkerhelper.model.entities.Guide

class StepsUpdater(
    private val onUploadEndListener: OnUploadEndListener
) {

    companion object {
        private const val TAG = "StepsUpdater"
        fun delete(uid: String, stepsCounter: Int) {
            for (position in 0 until stepsCounter) {
                FirebaseStorage
                    .getInstance()
                    .reference
                    .child("images/$uid-$position")
                    .delete()
            }
        }
    }

    private var stepsList = ArrayList<Guide.Step?>()
    private var guide: Guide? = null

    fun start(items: ArrayList<StepsRecyclerViewAdapter.Item>, guide: Guide) {
        this.guide = guide
        uploadPhotos(items, guide.uid)
    }

    private fun uploadPhotos(items: ArrayList<StepsRecyclerViewAdapter.Item>, uid: String) {
        stepsList = ArrayList()
        for (i in 0 until items.size) {
            stepsList.add(null)
        }

        for (i in 0 until items.size) {
            val item = items[i]
            if (item.step!!.photoUrl.startsWith("https://firebasestorage") && item.uri == null) {
                endUpload(Uri.parse(item.step!!.photoUrl), item.step!!.content, i)
            } else if (item.uri != null) {
                uploadPhoto(item.uri!!, i, uid, item.step!!.content)
            } else {
                deletePhotoIfExists(i, uid, item.step!!.content)
            }
        }
    }

    private fun uploadPhoto(uri: Uri, position: Int, uid: String, content: String) {
        FirebaseStorage
            .getInstance()
            .reference
            .child("images/$uid-$position")
            .putFile(uri)
            .addOnSuccessListener {
                FirebaseStorage
                    .getInstance()
                    .reference
                    .child("images/$uid-$position")
                    .downloadUrl
                    .addOnSuccessListener {
                        endUpload(it, content, position)
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Fail during getting new photo uri!", it)
                    }
            }
            .addOnFailureListener {
                Log.e(TAG, "Fail during uploading photo!", it)
            }
    }

    private fun deletePhotoIfExists(position: Int, uid: String, content: String) {
        FirebaseStorage
            .getInstance()
            .reference
            .child("images/$uid-$position")
            .delete()
            .addOnCompleteListener {
                endUpload(null, content, position)
            }
    }

    private fun endUpload(uri: Uri?, content: String, position: Int) {
        Log.d(TAG, "endUpload position = $position")
        if (uri != null) {
            stepsList[position] = Guide.Step(content, uri.toString())
        } else {
            stepsList[position] = Guide.Step(content)
        }
        if (!stepsList.contains(null)) {
            val tempList = ArrayList<Guide.Step>()
            stepsList.forEach {
                tempList.add(it!!)
            }
            guide!!.steps = tempList
            onUploadEndListener.onUploadEnd(guide!!)
        }
    }

    interface OnUploadEndListener {
        fun onUploadEnd(guide: Guide)
    }
}