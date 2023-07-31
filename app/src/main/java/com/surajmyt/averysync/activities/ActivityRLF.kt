package com.surajmyt.averysync.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.surajmyt.averysync.R

// RLF - REGISTRATION, LOG IN AND FORGOT PASSWORD
class ActivityRLF : HelperActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rlf)

        val signUpBtn = findViewById<Button>(R.id.sign_up_btn_rlf)
        val logInBtn = findViewById<Button>(R.id.log_in_btn_rlf)
        val resetPwdBtn = findViewById<Button>(R.id.reset_pwd_btn_rlf)

        signUpBtn.setOnClickListener {
            startActivity(Intent(this@ActivityRLF, SignUp::class.java))
        }
        logInBtn.setOnClickListener {
            startActivity(Intent(this@ActivityRLF, LogIn::class.java))
        }
        resetPwdBtn.setOnClickListener {
            startActivity(Intent(this@ActivityRLF, ResetPassword::class.java))
        }
    }
}