package com.surajmyt.averysync.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.surajmyt.averysync.R
import com.surajmyt.averysync.models.Board
import com.surajmyt.averysync.realtime_database.FireBaseRDB
import com.surajmyt.averysync.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class NewBoard : HelperActivity() {

    private var mSelectedImgUri: Uri? = null
    private lateinit var boardImage: CircleImageView
    private lateinit var mUsrName: String
    private var mBoardImgUrl: String = ""

    private val readImagePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_board)

        val createBoardBtn = findViewById<Button>(R.id.brd_create_btn)
        boardImage = findViewById(R.id.brd_img)

        setUpActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUsrName = intent.getStringExtra(Constants.NAME).toString()
        }

        boardImage.setOnClickListener {
            if (
                ContextCompat.checkSelfPermission(
                    this, readImagePermission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.chooseImg(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(readImagePermission),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        createBoardBtn.setOnClickListener {
            if (mSelectedImgUri != null) {
                uploadBoardImg()
            }
            else {
                progressDialog(resources.getString(R.string.pls_wt))
                newBoardCreation()
            }
        }

    }

    private fun newBoardCreation() {
        val boardName = findViewById<EditText>(R.id.new_brd_name).text.toString()
        val assignedUsers = ArrayList<String>()
        assignedUsers.add(getCurrentUserID())

        val newBoard = Board(
            mBoardImgUrl,
            boardName,
            mUsrName,
            assignedUsers
        )

        FireBaseRDB().newBoardCreation(this, newBoard)
    }

    private fun uploadBoardImg() {
        progressDialog(resources.getString(R.string.pls_wt))

        val storageReference : StorageReference =
            FirebaseStorage.getInstance().reference.child(
                "BOARD_IMG" + System.currentTimeMillis() + "." + Constants.fetchFileExtension(this, mSelectedImgUri)
            )
        storageReference.putFile(mSelectedImgUri!!)
            .addOnSuccessListener {taskSnapShot ->
                Log.i("Board Img URL", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapShot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {uri ->
                        Log.i("Downloadable IMG URI", uri.toString())
                        mBoardImgUrl = uri.toString()
                        newBoardCreation()
                    }
            }
            .addOnFailureListener{exception ->
                Toast.makeText(this, "Failure message: ${exception.message}", Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
    }

    fun newBoardCreationSuccessTask() {
        hideProgressDialog()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.chooseImg(this)
        } else {
            Toast.makeText(
                this,
                "Permission Denied.\nAllow Storage Permission from app info.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (
            resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMG_REQ_CODE
            && data!!.data != null
        ){
            mSelectedImgUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImgUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(boardImage)
            }catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.new_board_toolbar)
        setSupportActionBar(toolbar)

        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_btn)
        supportActionBar?.title = resources.getString(R.string.new_brd_title)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}