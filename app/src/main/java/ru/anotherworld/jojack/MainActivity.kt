package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import ru.anotherworld.jojack.database.DatabaseHelper
import ru.anotherworld.jojack.database.MainDatabase


val database = MainDatabase()
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<LinearLayout>(R.id.layout)
        val logo = findViewById<ImageView>(R.id.logo)
        startAnimation(layout, logo)

        val goingOn = findViewById<TextView>(R.id.about_us)
        goingOn.setOnClickListener {
            val uri = Uri.parse("https://www.vk.com/@rebel_jack-jojack")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        val login = findViewById<EditText>(R.id.login)
        val password = findViewById<EditText>(R.id.password)

        val enter = findViewById<Button>(R.id.enter)
        enter.setOnClickListener {
            if (true){ //Check internet connection
            //Connect to server, communication
            //Create Database
                val mContext = this@MainActivity
                mContext.startActivity(Intent(mContext, MainApp::class.java))
            }
            else Toast.makeText(this, getText(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
}

fun startAnimation(layout: LinearLayout, logo: ImageView){
    val tr = layout.background as TransitionDrawable
    tr.startTransition(800)

//    val tr2 = logo.background as TransitionDrawable
//    tr2.startTransition(800)

    val fadeIn = AlphaAnimation(0f, 1f)
    fadeIn.interpolator = DecelerateInterpolator() //add this
    fadeIn.duration = 2000

    val animation = AnimationSet(false) //change to false
    animation.addAnimation(fadeIn)
    logo.animation = animation
}


