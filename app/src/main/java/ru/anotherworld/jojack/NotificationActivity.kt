@file:OptIn(ExperimentalMaterial3Api::class)

package ru.anotherworld.jojack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ru.anotherworld.jojack.ui.theme.JoJackTheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Content()
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(){
    val context = LocalContext.current
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(colorResource(id = R.color.black2))) {
        TopAppBar(title = {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .background(colorResource(id = R.color.background2))
                .align(Alignment.CenterHorizontally)) {
                IconButton(onClick = { context.startActivity(Intent(context, MainApp::class.java)) },
                    modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(painterResource(id = R.drawable.arrow_back), null,
                        tint = colorResource(id = R.color.white))
                }
                Text(text = stringResource(id = R.string.notification),
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.align(Alignment.CenterVertically))
            }
        }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
            colors = TopAppBarDefaults
                .topAppBarColors(containerColor = colorResource(id = R.color.background2),
                    titleContentColor = colorResource(id = R.color.background2)))
        LazyColumn(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)){
            item{
                Notificator(head = "Уведомление",
                    text = "Вам пришло новое сообщение! Нажмите, чтобы прочитать")
            }
        }
    }

}

@Composable
private fun Notificator(head: String, text: String){
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(
            colorResource(id = R.color.background2),
            shape = RoundedCornerShape(20.dp)
        )
        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)) {
        Text(text = head, fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
            color = colorResource(id = R.color.white))
        Text(text = text, fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
            color = colorResource(id = R.color.white))
    }
}