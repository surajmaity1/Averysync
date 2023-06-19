package com.surajmyt.averysync.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.surajmyt.averysync.R
import com.surajmyt.averysync.models.User
import com.surajmyt.averysync.realtime_database.FireBaseRDB
import com.surajmyt.averysync.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.io.IOException

class UsrProfile : HelperActivity() {

    private lateinit var usrImg: CircleImageView
    lateinit var usrProfUpdateBtn: Button
    lateinit var mUsrDetails: User
    private var mSelectedImgUri: Uri? = null
    private var mUsrProfImgUrl: String = ""
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMG_REQ_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usr_profile)

        usrImg = findViewById(R.id.usr_prof_cir_img)
        usrProfUpdateBtn = findViewById(R.id.usr_prof_btn)

        setUpActionBar()

        FireBaseRDB().fetchUsrDetails(this)

        usrImg.setOnClickListener {
            if (
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                chooseImg()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        usrProfUpdateBtn.setOnClickListener {
            if (mSelectedImgUri != null) {
                uploadUsrImg()
            }
            else {
                progressDialog(resources.getString(R.string.pls_wt))
                updateUsrProfileInfo()
            }
        }

    }

    fun showUsrData(usr: User) {
        val name = findViewById<TextView>(R.id.usr_prof_nam)
        val email = findViewById<TextView>(R.id.usr_prof_mail)
        val mob = findViewById<TextView>(R.id.usr_prof_mob)

        mUsrDetails = usr

        Glide
            .with(this)
            .load(usr.img)
            .centerCrop()
            .placeholder(R.drawable.profile_icon)
            .into(usrImg)

        name.text = usr.name
        email.text = usr.email
        if (usr.mobile != 0L) mob.text = usr.mobile.toString()
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.usr_prof_toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_btn)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseImg()
        } else {
            Toast.makeText(
                this@UsrProfile,
                "Permission Denied.\nAllow Storage Permission from app info.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun chooseImg() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMG_REQ_CODE)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (
            resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMG_REQ_CODE
            && data!!.data != null
        ){
            mSelectedImgUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImgUri)
                    .centerCrop()
                    .placeholder(R.drawable.profile_icon)
                    .into(usrImg)
            }catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUsrProfileInfo() {

        val name = findViewById<EditText>(R.id.usr_prof_nam).text.toString()
        val mobile = findViewById<EditText>(R.id.usr_prof_mob).text.toString().toLong()

        val usrHashMap = HashMap<String, Any>()

        if (mUsrProfImgUrl.isNotEmpty() && mUsrProfImgUrl != mUsrDetails.img) {
            usrHashMap[Constants.IMG] = mUsrProfImgUrl
        }
        if (name != mUsrDetails.name) {
            usrHashMap[Constants.NAME] = name
        }
        if (mobile != mUsrDetails.mobile) {
            usrHashMap[Constants.MOBILE] = mobile
        }

        FireBaseRDB().updateUsrProfileInfo(this, usrHashMap)
    }

    private fun uploadUsrImg() {
        progressDialog(resources.getString(R.string.pls_wt))

        if (mSelectedImgUri != null) {
            val strRef: StorageReference = FirebaseStorage
                .getInstance().reference.child(
                    "USR_IMG" + System.currentTimeMillis()
                            + "." + fetchFileExtension(mSelectedImgUri)
                )
            strRef.putFile(mSelectedImgUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i(
                        "Firebase ImgURL", taskSnapshot
                            .metadata!!.reference!!.downloadUrl.toString()
                    )

                    taskSnapshot
                        .metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->

                            Log.i("Downloadable ImgURL", uri.toString())
                            mUsrProfImgUrl = uri.toString()

                            updateUsrProfileInfo()
                        }
                        .addOnFailureListener { ex ->
                            hideProgressDialog()
                            Toast.makeText(this@UsrProfile, ex.message, Toast.LENGTH_SHORT).show()
                        }

                }
        }

    }
    private fun fetchFileExtension(uri: Uri?): String? = MimeTypeMap
        .getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))

    fun updateUsrProfSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}