package com.surajmyt.averysync.realtime_database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.surajmyt.averysync.activities.SignUp
import com.surajmyt.averysync.models.User
import com.surajmyt.averysync.utils.Constants

class FireBaseRDB {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUp, user: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                FirebaseAuth.getInstance().signOut()
            }.addOnFailureListener { exception ->
                Log.e(activity.javaClass.simpleName,"Error while getting loggedIn user details", exception)
            }
    }

    private fun getCurrentUserId():String{
        var currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId = ""

        if (currentUser != null){
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

}