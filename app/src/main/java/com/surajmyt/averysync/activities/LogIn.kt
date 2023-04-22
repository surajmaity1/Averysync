package com.surajmyt.averysync.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.surajmyt.averysync.R

class LogIn : HelperActivity() {

    private val tag = "LogIn"

    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        firebaseAuth = FirebaseAuth.getInstance()
        val signBtn = findViewById<Button>(R.id.log_in_act_btn)

        signBtn.setOnClickListener {
            logInRegisteredUser()
        }


        setUpActionBar()
    }

    private fun logInRegisteredUser() {

        val editEmail = findViewById<EditText>(R.id.email_et_log_in)
        val editPassword = findViewById<EditText>(R.id.pwd_et_log_in)

        val mail : String = editEmail.text.toString().trim() {it <= ' '}
        val pwd : String = editPassword.text.toString()

        if (validateForm(mail, pwd)) {
            progressDialog(resources.getString(R.string.pls_wt))

            firebaseAuth.signInWithEmailAndPassword(mail, pwd)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        if (isMailVerified()) {
                            Log.d(tag, "signInWithEmail:success")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this, "Email verification is required",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.w(tag, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Error: ${task.exception!!.message.toString()}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun isMailVerified() : Boolean{
        val user = firebaseAuth.currentUser

        if (user != null){
            val emailVerifier = user.isEmailVerified
            if (emailVerifier) return true
        }

        return false
    }


    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.log_in_toolbar)
        setSupportActionBar(toolbar)

        val supportActionBar = supportActionBar

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_btn)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateForm(mail : String, pwd : String) : Boolean{
        return when {
            TextUtils.isEmpty(mail)->{
                showErrorSnackBar("Please Enter Email")
                false
            }
            TextUtils.isEmpty(pwd)->{
                showErrorSnackBar("Please Enter Password")
                false
            } else ->{
                true
            }
        }
    }
}