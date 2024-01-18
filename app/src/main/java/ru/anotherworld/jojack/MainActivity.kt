package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import ru.anotherworld.jojack.database.MainDatabase


val database = MainDatabase()
@SuppressLint("StaticFieldLeak")
var _context: Context? = null
var timeLogin: String? = null
var timeHashPassword: String? = null
val cipher = Cipher()
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = MyClient()
        Thread(Runnable { client.main() }).start()
        _context = this@MainActivity
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
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
            try {
                timeLogin = login.text.toString()
                timeHashPassword = cipher.hash(password.text.toString())
                Thread(Runnable {
                    if(timeLogin != "" && timeHashPassword != "") {
                        reg = true
//                        client.sendToServer("""NU:{"login":"$timeLogin","hashPassword":"$timeHashPassword"}""")
                    }
                    else {
                        database.setId(0)
                        startActivity(Intent(this@MainActivity, MainApp::class.java))
                    }
                }).start()
            } catch (e: Exception){
                Toast.makeText(this, getText(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
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

fun showErrorEntered(){
    try{
        Toast.makeText(_context, _context!!.getText(R.string.error_enter), Toast.LENGTH_SHORT).show()
    } catch (e: Exception){
        println(e.message)
    }
}

fun showError(){
    try {
        Toast.makeText(_context, _context!!.getText(R.string.error), Toast.LENGTH_SHORT).show()
    } catch (e: Exception){
        println(e.message)
    }
}

fun startNewActivity() = _context!!.startActivity(Intent(_context, MainApp::class.java))

