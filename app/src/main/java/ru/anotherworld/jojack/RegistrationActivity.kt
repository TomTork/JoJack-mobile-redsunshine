@file:OptIn(ExperimentalMaterial3Api::class)

package ru.anotherworld.jojack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.anotherworld.jojack.database.MainData
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                RegistrationContent2()
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationContent2(){
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    Column(modifier = Modifier
        .fillMaxSize(1f)
        .background(colorResource(id = R.color.background_lr))) {
        Image(
            painterResource(id = R.drawable.jojacks_fixed_optimized), null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(250.dp)
                .padding(top = 15.dp))
        Text(text = "JoJack", fontFamily = interFamily, fontWeight = FontWeight.W600,
            fontSize = 40.sp, modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset(y = (-35).dp),
            color = colorResource(id = R.color.white))
        Column(modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center) {
            TextField(value = login, onValueChange = { login = it },
                placeholder = { Text(text = stringResource(id = R.string.enter_login),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 3.dp)
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.white)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(id = R.color.background_field),
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                leadingIcon = { Icon(
                    painterResource(id = R.drawable.account_lr), null,
                    tint = Color.White) },
                modifier = Modifier.padding(start=19.dp, end=19.dp, top=20.dp)
                    .fillMaxWidth(1f))
            TextField(value = password, onValueChange = { password = it },
                placeholder = { Text(text = stringResource(id = R.string.enter_password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 3.dp)
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.white)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(id = R.color.background_field),
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                leadingIcon = { Icon(
                    painterResource(id = R.drawable.password_lr), null,
                    tint = Color.White) },
                modifier = Modifier.padding(start=19.dp, end=19.dp, top=30.dp)
                    .fillMaxWidth(1f))
            ElevatedButton(onClick = {
                if (login != "" && password != "" && login.length < 64){
                    val reg = Register()
                    coroutine.launch {
                        mDatabase.init(MainData("", "", 0, 0, -1,
                            false, "", "", "", "", "", ""))
                        val token1 = reg.reg(login, password, coroutine).substringAfter(":\"")
                            .substringBefore("\"}")
                        if(token1 != ""){
                            mDatabase.setLogin(login)
                            mDatabase.setToken(token1)
                            context.startActivity(Intent(context, MainApp::class.java))
                        }
                        else Toast.makeText(context, context.getText(R.string.user_exists), Toast.LENGTH_SHORT).show()
                    }
                }
            },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 10.dp, end = 10.dp, top = 30.dp)
                    .fillMaxWidth(0.9f)
                    .alpha(if (login != "" && password != "") 1f else 0.8f),
                colors = ButtonDefaults
                    .buttonColors(containerColor = colorResource(id = R.color.background_lr_button)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = stringResource(id = R.string.register),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 13.sp,
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
                .padding(bottom = 20.dp)
                .fillMaxWidth(1f)) {
            AnnotatedClickableText2(part1 = stringResource(id = R.string.exists_account),
                part2 = stringResource(id = R.string.log)) {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        }

    }
}
@Composable
fun AnnotatedClickableText2(part1: String, part2: String, action: () -> Unit) {
    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.Gray,
                fontFamily = interFamily,
                fontWeight = FontWeight.W500
            )
        ) { append("$part1 ") }
        pushStringAnnotation(
            tag = "SignUp",
            annotation = "SignUp"
        )
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.background_lr_text),
                fontFamily = interFamily,
                fontWeight = FontWeight.W500)
        ) {append(part2) }
        pop()
    }
    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = "SignUp",
                start = offset,
                end = offset
            )[0].let { annotation ->
                action()
            }
        }
    )
}
