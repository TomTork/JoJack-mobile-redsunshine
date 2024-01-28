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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.ui.theme.JoJackTheme
import java.net.ConnectException

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                LoginContent2()
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginContent2(){
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val interFamily = FontFamily(
        Font(R.font.inter600, FontWeight.W600),
        Font(R.font.inter_medium500, FontWeight.W500)
    )
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    Column(modifier = Modifier
        .fillMaxSize(1f)
        .background(colorResource(id = R.color.background_lr))) {
        Image(painterResource(id = R.drawable.jojacks_fixed_optimized), null,
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
                leadingIcon = { Icon(painterResource(id = R.drawable.account_lr), null,
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
                leadingIcon = { Icon(painterResource(id = R.drawable.password_lr), null,
                    tint = Color.White) },
                modifier = Modifier.padding(start=19.dp, end=19.dp, top=30.dp)
                    .fillMaxWidth(1f))
            ElevatedButton(onClick = {
                try{
                    if (login != "" && password != ""){
                        val log = Login()
                        coroutine.launch {
                            val token = log.log(login, password).substringAfter(":\"")
                                .substringBefore("\"}")
                            if(token == "NF") Toast.makeText(context, context.getText(R.string.nf),
                                Toast.LENGTH_SHORT).show()
                            else if(token == "PL") Toast.makeText(context, context.getText(R.string.pl),
                                Toast.LENGTH_SHORT).show()
                            else if(token != ""){
                                mDatabase.setLogin(login)
                                mDatabase.setToken(token)
                                context.startActivity(Intent(context, MainApp::class.java))
                            }
                            else Toast.makeText(context, context.getText(R.string.error),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: ConnectException){
                    Toast.makeText(context, context.getText(R.string.error), Toast.LENGTH_SHORT).show()
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
                Text(text = stringResource(id = R.string.enter),
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
            AnnotatedClickableText(part1 = stringResource(id = R.string.no_account),
                part2 = stringResource(id = R.string.reg)) {
                context.startActivity(Intent(context, RegistrationActivity::class.java))
            }
        }

    }
}
@Composable
fun AnnotatedClickableText(part1: String, part2: String, action: () -> Unit) {
    val interFamily = FontFamily(
        Font(R.font.inter600, FontWeight.W600),
        Font(R.font.inter_medium500, FontWeight.W500)
    )
    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.Gray,
                fontFamily = interFamily,
                fontWeight = FontWeight.W500
            )) { append("$part1 ") }
        pushStringAnnotation(
            tag = "SignUp",
            annotation = "SignUp"
        )
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.background_lr_text),
                fontFamily = interFamily,
                fontWeight = FontWeight.W500)) {append(part2) }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginContent(){
    var password by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
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
        Column(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Column {
                var isVisible by remember { mutableStateOf(false) }
                val maxLength = 30
                val lightBlue = colorResource(id = R.color.background_text_field)
                val blue = Color.White
                TextField(
                    placeholder = {Text(
                        text = stringResource(id = R.string.login),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 3.dp)
                            .alpha(0.7f),
                        textAlign = TextAlign.Start,
                        color = blue
                    )},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    value = login,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = lightBlue,
                        cursorColor = Color.White,
                        disabledLabelColor = lightBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = {
                        if (it.length <= maxLength) {login = it}
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    trailingIcon = {
                        if (login.isNotEmpty()) {
                            IconButton(onClick = { login = "" }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
                Text(
                    text = "${login.length} / $maxLength",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp, end = 20.dp, bottom = 3.dp)
                        .alpha(if (isVisible) 1f else 0f),
                    textAlign = TextAlign.End,
                    color = blue
                )
                isVisible = login.length > 20
            }

            Column {
                var isVisible2 by remember { mutableStateOf(false) }
                val maxLength = 30
                val lightBlue = colorResource(id = R.color.background_text_field)
                val blue = Color.White
                TextField(
                    placeholder = {Text(
                        text = stringResource(id = R.string.password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 3.dp)
                            .alpha(0.7f),
                        textAlign = TextAlign.Start,
                        color = blue
                    )},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    value = password,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = lightBlue,
                        cursorColor = Color.White,
                        disabledLabelColor = lightBlue,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = {
                        if (it.length <= maxLength) {password = it}
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    trailingIcon = {
                        if (password.isNotEmpty()) {
                            IconButton(onClick = { password = "" }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
                Text(
                    text = "${password.length} / $maxLength",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp, end = 20.dp, bottom = 3.dp)
                        .alpha(if (isVisible2) 1f else 0f),
                    textAlign = TextAlign.End,
                    color = blue
                )
                isVisible2 = password.length > 20
            }

            ElevatedButton(onClick = {
                 if (login != "" && password != ""){
                     val log = Login()
                     coroutine.launch {
                         val token = log.log(login, password).substringAfter(":\"")
                             .substringBefore("\"}")
                         if(token == "NF") Toast.makeText(context, context.getText(R.string.nf),
                             Toast.LENGTH_SHORT).show()
                         else if(token == "PL") Toast.makeText(context, context.getText(R.string.pl),
                             Toast.LENGTH_SHORT).show()
                         else if(token != ""){
                             mDatabase.setLogin(login)
                             mDatabase.setToken(token)
                             context.startActivity(Intent(context, MainApp::class.java))
                         }
                         else Toast.makeText(context, context.getText(R.string.error),
                             Toast.LENGTH_SHORT).show()
                     }
                 }
            },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    .fillMaxWidth(0.9f)
                    .alpha(if (login != "" && password != "") 1f else 0.8f),
                colors = ButtonDefaults
                    .buttonColors(containerColor = colorResource(id = R.color.white)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.enter),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    color = colorResource(id = R.color.black)
                )
            }
        }
        Column(verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(1f)
                .padding(bottom = 10.dp)
                .fillMaxWidth(1f)) {
            Text(text = stringResource(id = R.string.to_register),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        context.startActivity(
                            Intent(
                                context,
                                RegistrationActivity::class.java
                            )
                        )
                    },
                color = colorResource(id = R.color.ocen_blue),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
