package com.surajmyt.averysync.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.surajmyt.averysync.R

open class HelperActivity : AppCompatActivity() {

    // This exit condition helps to implement
    // double back to exit pressed once ...
    private var exitCondition = false

    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper)


    }

    fun progressDialog(text: String) {
        progressDialog = Dialog(this)

        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.findViewById<TextView>(R.id.pg_txt_view).text = text
        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    fun getCurrentUserID() = FirebaseAuth
        .getInstance().currentUser!!.uid

    fun doubleBackToExit() {
        if (exitCondition) {
            super.onBackPressed()
            return
        }

        this.exitCondition = true

        Toast.makeText(
            this@HelperActivity,
            resources.getString(R.string.pls_wt_ext),
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({exitCondition = false}, 1500)
    }

    fun showErrorSnackBar(msg: String) {
        val snackBar = Snackbar
            .make(
                findViewById(android.R.id.content),
                msg,
                Snackbar.LENGTH_LONG
            )

        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(this, R.color.snack_bar_err_color)
        )
        
        snackBar.show()
    }
}