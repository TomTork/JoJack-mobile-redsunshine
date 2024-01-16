package ru.anotherworld.jojack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class Terminal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            JoJackTheme {
                WorkWithFunctions()
            }
        }
    }
}

@Composable
private fun WorkWithFunctions(){
    Text(text = "Terminal activity", color = colorResource(id = R.color.white))
}