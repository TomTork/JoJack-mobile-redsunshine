package ru.anotherworld.jojack.elements

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.ui.theme.JoJackTheme

//var idChat3: String = ""
//var nameChat3: String = ""
//var iconChat3: String = ""
//var repost: String = ""

class Chat3 : ComponentActivity() {
    override fun onDestroy() {
        super.onDestroy()
        Log.d("DESTROY", "START")
//        GlobalScope.launch {
//            destroy!!.closeSession()
//        }
        Log.d("DESTROY", "END")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JoJackTheme {
        Greeting("Android")
    }
}