package ru.anotherworld.jojack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class ChangePassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            JoJackTheme {
                ChangePass()
            }
        }
    }
}

@Composable
private fun ChangePass(){
    Text(text = "Change password", color = colorResource(id = R.color.white))
} 