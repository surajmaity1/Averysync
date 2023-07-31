package com.surajmyt.averysync.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS : String = "users"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val IMG: String = "img"
    const val BOARDS: String = "boards"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOC_ID: String = "docId"
    const val TASK_LIST = "taskList"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMG_REQ_CODE = 2
    fun chooseImg(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMG_REQ_CODE)
    }
    fun fetchFileExtension(activity: Activity, uri: Uri?): String? = MimeTypeMap
        .getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
}