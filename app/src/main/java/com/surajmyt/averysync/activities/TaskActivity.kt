package com.surajmyt.averysync.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.surajmyt.averysync.R
import com.surajmyt.averysync.adapters.TaskItemAdapter
import com.surajmyt.averysync.models.Board
import com.surajmyt.averysync.models.Task
import com.surajmyt.averysync.realtime_database.FireBaseRDB
import com.surajmyt.averysync.utils.Constants

class TaskActivity : HelperActivity() {

    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        var docId: String = ""

        if (intent.hasExtra(Constants.DOC_ID)) {
            docId = intent.getStringExtra(Constants.DOC_ID).toString()
        }

        progressDialog(resources.getString(R.string.pls_wt))
        FireBaseRDB().fetchBoardDetails(this, docId)
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.task_activity_toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_btn)
            actionBar.title = mBoardDetails.name
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    fun boardDetails(board: Board) {
        mBoardDetails = board

        hideProgressDialog()
        setUpActionBar()


        val taskList = Task(resources.getString(R.string.crt_lst))
        board.taskList.add(taskList)

        val taskRecyclerView = findViewById<RecyclerView>(R.id.task_actv_rec_view)
        taskRecyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        taskRecyclerView.setHasFixedSize(true)

        val adapter = TaskItemAdapter(this, board.taskList)
        taskRecyclerView.adapter = adapter
    }
    fun createUpdateTaskList() {
        hideProgressDialog()

        progressDialog(resources.getString(R.string.pls_wt))

        FireBaseRDB().fetchBoardDetails(this, mBoardDetails.docId)
    }
    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FireBaseRDB().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        progressDialog(resources.getString(R.string.pls_wt))

        FireBaseRDB().createUpdateTaskList(this, mBoardDetails)
    }
}