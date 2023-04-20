package com.surajmyt.averysync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

// RLF - REGISTRATION, LOG IN AND FORGOT PASSWORD
class ActivityRLF : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rlf)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


    }
}