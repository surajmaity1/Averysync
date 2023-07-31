package com.surajmyt.averysync.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.surajmyt.averysync.R

class ResetPassword : HelperActivity() {

    private val tag = "ResetPassword"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val resetPwdBtn = findViewById<Button>(R.id.reset_pwd_act_btn)

        resetPwdBtn.setOnClickListener {
            resetPassword()
        }


        setUpActionBar()
    }

    private fun resetPassword(){

        val editEmail = findViewById<EditText>(R.id.email_et_reset_pwd)
        val mail : String = editEmail.text.toString().trim() {it <= ' '}

        if (mail.isNotEmpty()){

            progressDialog(resources.getString(R.string.pls_wt))

            FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                .addOnCompleteListener{ task ->

                    hideProgressDialog()
                    if (task.isSuccessful){

                        Toast.makeText(this,
                            "Email sent.\nChange password using attached link.",
                            Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, LogIn::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else{
            Toast.makeText(this,
                "Enter Email Address", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpActionBar(){
        val toolbar = findViewById<Toolbar>(R.id.reset_pwd_toolbar)
        setSupportActionBar(toolbar)

        val supportActionBar = supportActionBar

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_btn)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}