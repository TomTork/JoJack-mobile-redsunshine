@file:OptIn(ExperimentalMaterial3Api::class)

package ru.anotherworld.jojack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {

                RegistrationContent()
            }
        }
    }
}

@Composable
private fun RegistrationContent(){
    var login by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    val gradientColors = listOf(
        colorResource(id = R.color.sea_grren2),
        colorResource(id = R.color.sea_green))
    Column(modifier = Modifier
        .background(colorResource(id = R.color.ghost_white))
        .fillMaxSize(1f)) {
        Text(
            text = "JoJack",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            color = colorResource(id = R.color.white),
            fontSize = 35.sp
        )
//        Text(
//            text=buildAnnotatedString {
//                withStyle(style = SpanStyle(fontSize = 50.sp,
//                    fontWeight = FontWeight.ExtraBold)){
//                    append("Регистрация\n")
//                }
//                withStyle(style = SpanStyle(fontSize = 24.sp)){
//                    append("Добро пожаловать домой,\n")
//                }
//                withStyle(style = SpanStyle(fontSize = 24.sp)){
//                    append("Жожек!")
//                }
//            },
//            style=TextStyle(brush = Brush.linearGradient(colors=gradientColors)),
//            modifier = Modifier
//                .padding(top = 10.dp, start = 20.dp)
//                .shadow(10.dp, ambientColor = colorResource(id = R.color.blue_shadow))
//        )
        Column(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            InputText(caption = stringResource(id = R.string.login))
            InputText(caption = stringResource(id = R.string.password))
//            OutlinedTextField(value = login,
//                placeholder = { Text(text = stringResource(id = R.string.login),
//                    style = MaterialTheme.typography.labelLarge,
//                    color = colorResource(id = R.color.white)) },
//                onValueChange = { login = it },
//                modifier = Modifier
//                    .padding(start = 10.dp, end = 10.dp)
//                    .shadow(3.dp)
//                    .fillMaxWidth(0.9f),
//                colors = TextFieldDefaults
//                    .colors(cursorColor = colorResource(id = R.color.white),
//                        unfocusedContainerColor = Color.Transparent))
//            OutlinedTextField(value = password,
//                placeholder = { Text(text = stringResource(id = R.string.password),
//                    style = MaterialTheme.typography.labelLarge,
//                    color = colorResource(id = R.color.white))},
//                onValueChange = { password = it },
//                modifier = Modifier
//                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
//                    .shadow(3.dp)
//                    .fillMaxWidth(0.9f),
//                colors = TextFieldDefaults
//                    .colors(cursorColor = colorResource(id = R.color.white),
//                        unfocusedContainerColor = Color.Transparent))
            ElevatedButton(onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    .fillMaxWidth(0.9f),
                colors = ButtonDefaults
                    .buttonColors(containerColor = colorResource(id = R.color.register_button)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.register),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    color = colorResource(id = R.color.white)
                )
            }
        }
        Column(verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(1f)
                .padding(bottom = 10.dp)
                .fillMaxWidth(1f)) {
            Text(text = stringResource(id = R.string.to_enter),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                .align(Alignment.CenterHorizontally),
                color = colorResource(id = R.color.white)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(caption: String,){
    Column {
        var textState by remember { mutableStateOf("") }
        var isVisible by remember { mutableStateOf(false) }
        val maxLength = 30
        val lightBlue = colorResource(id = R.color.background_text_field)
        val blue = Color.White
        TextField(
            placeholder = {Text(
                text = caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp),
                textAlign = TextAlign.Start,
                color = blue
            )},
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            value = textState,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = lightBlue,
                cursorColor = Color.White,
                disabledLabelColor = lightBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.length <= maxLength) textState = it
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            trailingIcon = {
                if (textState.isNotEmpty()) {
                    IconButton(onClick = { textState = "" }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        Text(
            text = "${textState.length} / $maxLength",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp, end = 20.dp, bottom = 3.dp)
                .alpha(if (isVisible) 1f else 0f),
            textAlign = TextAlign.End,
            color = blue
        )
        isVisible = textState.length > 20

    }
}