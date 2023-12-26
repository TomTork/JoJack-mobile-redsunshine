package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class MainApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Content()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Content(){
    var visible by remember {
        mutableStateOf(true)
    }
    val scrollState = rememberScrollState()
    AnimatedVisibility(visible = visible,
        enter = slideInHorizontally(tween(durationMillis = 3000, easing = LinearEasing)) + expandHorizontally(expandFrom = Alignment.End) + fadeIn(tween(durationMillis = 3000, easing = LinearEasing)),
        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                + shrinkHorizontally() + fadeOut(tween(durationMillis = 3000)),
    ) {
        Scaffold(
            topBar = {
                Surface(modifier = Modifier.background(Color.Black)) {
                    Row {
                        Text(text = home!!)
                    }
                }

            },
            bottomBar = {
                Row {
                    Button(onClick = {  }) {

                    }
                    Button(onClick = { /*TODO*/ }) {

                    }
                    Button(onClick = { /*TODO*/ }) {

                    }
                }
            }
        ) {
            Column (modifier = Modifier.verticalScroll(scrollState)) {
                for(i in 1..50){
                    Text("$i", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp))
                }
            }
        }
    }

}

@Composable
fun ShowAnimatedText(
    text : String?,
    show: Boolean
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(animationSpec = tween(2000)),
            exit = fadeOut(animationSpec = tween(2000))
        ) {

            text?.let {
                Text(text = it)
            }
        }
    }
}