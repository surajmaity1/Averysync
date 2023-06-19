package com.surajmyt.averysync.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.surajmyt.averysync.R
import com.surajmyt.averysync.models.User
import com.surajmyt.averysync.realtime_database.FireBaseRDB
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : HelperActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val USR_PROFILE_REQ_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpActionBar()

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        FireBaseRDB().fetchUsrDetails(this)
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.dashboard_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)

        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.layout_drawer)
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.layout_drawer)

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            doubleBackToExit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.profile_nav ->{
                startActivityForResult(Intent(this@MainActivity, UsrProfile::class.java), USR_PROFILE_REQ_CODE)
            }
            R.id.log_out_nav -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, ActivityRLF::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                )
                startActivity(intent)
                finish()
            }
        }

        // User clicked somewhere else
        val drawerLayout = findViewById<DrawerLayout>(R.id.layout_drawer)
        drawerLayout.closeDrawer(GravityCompat.START)


        return true
    }

    fun updateNavUsrInfo(usr: User){
        val navUsrImg = findViewById<CircleImageView>(R.id.usr_img_nav)
        val usrTxtView = findViewById<TextView>(R.id.usr_name_nav)

        Glide
            .with(this)
            .load(usr.img)
            .centerCrop()
            .placeholder(R.drawable.profile_icon)
            .into(navUsrImg)

        usrTxtView.text = usr.name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == USR_PROFILE_REQ_CODE) {
            FireBaseRDB().fetchUsrDetails(this)
        }else {
            Log.e("MainActivity: ", "User Profile data not updated.")
        }
    }
}