@file:OptIn(ExperimentalMaterial3Api::class)

package ru.anotherworld.jojack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationContent(){
//    val dataList = mainViewModel.dataList.collectAsState(initial = emptyList())
//    println(dataList.value) //Количество строк в таблице
    var password by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
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
                val reg = Register()
                coroutine.launch {
                    val token1 = reg.reg(login, password).substringAfter(":\"")
                        .substringBefore("\"}")
                    if(token1 != ""){
                        mDatabase.setLogin(login)
                        mDatabase.setToken(token1)
                        context.startActivity(Intent(context, MainApp::class.java))
                    }
                    else Toast.makeText(context, context.getText(R.string.user_exists), Toast.LENGTH_SHORT).show()
                }
            },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    .fillMaxWidth(0.9f)
                    .alpha(if (login != "" && password != "") 1f else 0.8f),
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
                    .align(Alignment.CenterHorizontally)
                    .clickable { context.startActivity(Intent(context, LoginActivity::class.java)) },
                color = colorResource(id = R.color.ocen_blue),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Serializable
private data class RowToken(
    val rowToken: String
)