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
import com.google.firebase.auth.FirebaseUser
import com.surajmyt.averysync.R
import com.surajmyt.averysync.models.User
import com.surajmyt.averysync.realtime_database.FireBaseRDB

class SignUp : HelperActivity() {

    private val tag = "SignUp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signUpBtn = findViewById<Button>(R.id.sign_up_act_btn)
        signUpBtn.setOnClickListener {
            registerUser()
        }

        setUpActionBar()
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.sign_up_toolbar)
        setSupportActionBar(toolbar)

        val supportActionBar = supportActionBar

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_btn)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser(){

        val editName = findViewById<EditText>(R.id.name_et_sign_up)
        val editEmail = findViewById<EditText>(R.id.email_et_sign_up)
        val editPassword = findViewById<EditText>(R.id.pwd_et_sign_up)

        val name : String = editName.text.toString().trim() {it <= ' '}
        val email : String = editEmail.text.toString().trim() {it <= ' '}
        val password : String = editPassword.text.toString()

        if(validateForm(name, email, password)){
            progressDialog(resources.getString(R.string.pls_wt))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!

                        val user = User(firebaseUser.uid, name, registeredEmail)

                        FireBaseRDB().registerUser(this, user)
                        sendEmailVerification()
                    } else {
                        hideProgressDialog()
                        Toast.makeText(
                            this,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun sendEmailVerification(){
        hideProgressDialog()
        val user = FirebaseAuth.getInstance().currentUser!!

        user.sendEmailVerification()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    Log.d(tag, "Email sent.")
                    Toast.makeText(
                        this, "Verification email is sent to your mail.\nVerify your mail to Log In.",
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LogIn::class.java)
                    startActivity(intent)
                    finish()
                }
            }

    }

    private fun validateForm(nam : String, mail : String, pwd : String) : Boolean{
        return when {
            TextUtils.isEmpty(nam)->{
                showErrorSnackBar("Please Enter Name")
                false
            }
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