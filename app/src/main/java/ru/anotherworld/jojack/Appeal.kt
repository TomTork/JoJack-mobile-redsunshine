package ru.anotherworld.jojack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class Appeal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Content(){
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(color = colorResource(id = R.color.black2))) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .background(colorResource(id = R.color.background2))
                        .align(Alignment.CenterHorizontally)
                ) {
                    IconButton(
                        onClick = { context.startActivity(Intent(context, MainApp::class.java)) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.arrow_back), null,
                            tint = colorResource(id = R.color.white)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.appeal),
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600
                    )
                }
            }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
            colors = TopAppBarDefaults
                .topAppBarColors(
                    containerColor = colorResource(id = R.color.background2),
                    titleContentColor = colorResource(id = R.color.background2)
                ))
        Column(modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)) {
            TextField(value = name, onValueChange = { name = it },
                placeholder = { Text(text = stringResource(id = R.string.enter_name),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(start = 3.dp)
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.W500,
                    color = colorResource(id = R.color.white)) },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(id = R.color.background_field),
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
                    .background(
                        color = colorResource(id = R.color.background2),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .fillMaxWidth(1f))
            TextField(value = text, onValueChange = { text = it },
                placeholder = { Text(text = stringResource(id = R.string.enter_appeal),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(start = 3.dp)
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.W500,
                    color = colorResource(id = R.color.white)) },
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(id = R.color.background_field),
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                    .background(
                        color = colorResource(id = R.color.background2),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.9f))
            Button(onClick = { /*Отправить обращение*/ },
                colors = ButtonDefaults
                    .buttonColors(containerColor = colorResource(id = R.color.background_lr_button)),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(20.dp)) {
                Text(text = stringResource(id = R.string.send_appeal),
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.W500,
                    color = colorResource(id = R.color.white))
            }
        }
    }
}