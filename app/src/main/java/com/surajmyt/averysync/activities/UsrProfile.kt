package com.surajmyt.averysync.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.surajmyt.averysync.R
import com.surajmyt.averysync.models.User
import com.surajmyt.averysync.realtime_database.FireBaseRDB
import de.hdodenhof.circleimageview.CircleImageView

class UsrProfile : HelperActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usr_profile)

        setUpActionBar()

        FireBaseRDB().fetchUsrDetails(this)
    }

    fun showUsrData(usr: User) {
        val usrImg = findViewById<CircleImageView>(R.id.usr_prof_cir_img)
        val name = findViewById<TextView>(R.id.usr_prof_nam)
        val email = findViewById<TextView>(R.id.usr_prof_mail)
        val mob = findViewById<TextView>(R.id.usr_prof_mob)

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
}