package com.surajmyt.averysync.realtime_database

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.surajmyt.averysync.activities.LogIn
import com.surajmyt.averysync.activities.MainActivity
import com.surajmyt.averysync.activities.NewBoard
import com.surajmyt.averysync.activities.SignUp
import com.surajmyt.averysync.activities.TaskActivity
import com.surajmyt.averysync.activities.UsrProfile
import com.surajmyt.averysync.models.Board
import com.surajmyt.averysync.models.User
import com.surajmyt.averysync.utils.Constants

class FireBaseRDB {

    private val mFireStore = FirebaseFirestore.getInstance()
    private val tag = "FireBaseRDB"
    fun fetchUsrDetails(activity: Activity, readBrdLstNeeded: Boolean = false) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .get()
            .addOnSuccessListener { docs ->
                val loggedUser = docs.toObject(User::class.java)!!

                when(activity) {
                    is MainActivity -> {
                        activity.updateNavUsrInfo(loggedUser, readBrdLstNeeded)
                    }
                    is UsrProfile -> {
                        activity.showUsrData(loggedUser)
                    }
                    is LogIn -> {
                        activity.logInSuccess(loggedUser)
                    }
                }
            }
            .addOnFailureListener { exception ->
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is LogIn -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(tag, "Error msg: ${exception.message}")
            }
    }

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

    fun newBoardCreation(activity: NewBoard, board: Board) {
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "New Board Created")
                Toast.makeText(activity, "New Board Created", Toast.LENGTH_SHORT).show()
                activity.newBoardCreationSuccessTask()
            }
            .addOnFailureListener {exception ->
                Log.e(activity.javaClass.simpleName, "Board creation failed", exception)
                Toast.makeText(activity, "Board creation failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    fun getCurrentUserId():String{
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId = ""

        if (currentUser != null){
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    fun updateUsrProfileInfo(activity: UsrProfile, usrHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(usrHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Info updated successfully.")
                Toast.makeText(activity, "Profile Info updated.", Toast.LENGTH_SHORT).show()
                activity.updateUsrProfSuccess()
            }
            .addOnFailureListener {e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Failed While Updating Profile Info.", e)
                Toast.makeText(activity, "Failed While Updating Profile Info.", Toast.LENGTH_SHORT).show()
            }

    }

    fun fetchBoardList(activity: MainActivity) {
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener { documentsSnapShots ->

                Log.i(activity.javaClass.simpleName, documentsSnapShots.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()

                for (document in documentsSnapShots) {
                    val eachBoard = document.toObject(Board::class.java)!!
                    eachBoard.docId = document.id
                    boardList.add(eachBoard)
                }
                activity.attachBoardListToUI(boardList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: Fetching Board from FireStore Failed")
            }
    }
    fun fetchBoardDetails(activity: TaskActivity, docId: String){

        mFireStore.collection(Constants.BOARDS)
            .document(docId)
            .get()
            .addOnSuccessListener { documentsSnapShots ->

                Log.i(activity.javaClass.simpleName, documentsSnapShots.toString())

                val board = documentsSnapShots.toObject(Board::class.java)!!
                board.docId = documentsSnapShots.id
                activity.boardDetails(board)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: Fetching Board Details from FireStore Failed")
            }
    }
    fun createUpdateTaskList(activity: TaskActivity, board: Board) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.docId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Task Operation [Add or Update] Performed.")
                activity.createUpdateTaskList()
            }
            .addOnFailureListener {exception ->
                Log.e(activity.javaClass.simpleName, "Error: Task Operation [Add or Update] Failed.", exception)
            }
    }
}