package ru.anotherworld.jojack.elements

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.anotherworld.jojack.MainApp
import ru.anotherworld.jojack.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Chat(idChat: Int = 0, nameChat: String = "Флудилка", users: List<String>? = null,
         iconChat: String? = "", messages: List<Pair<String, String>>? = null){
    val context = LocalContext.current
    val nunitoFamily = FontFamily(Font(R.font.nunito_medium500, FontWeight.W500))
    var state by remember { mutableStateOf("Онлайн") } //Состояние в чате: кто-то печатает и т.д.
    var message by remember { mutableStateOf("") }
    Scaffold(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .background(color = colorResource(id = R.color.background2)),
        topBar = {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .background(color = colorResource(id = R.color.background2))) {
                IconButton(onClick = { context.startActivity(Intent(context, MainApp::class.java)) }){
                    Icon(painterResource(id = R.drawable.arrow_back), null,
                        tint = colorResource(id = R.color.white),
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.CenterVertically))
                }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(iconChat)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.preview),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                )
                Column(modifier = Modifier
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp)) {
                    Text(text = nameChat, fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                        fontSize = 23.sp, color = colorResource(id = R.color.white))
                    Text(text = state, fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                        fontSize = 17.sp, modifier = Modifier
                            .offset(y = (-6).dp)
                            .alpha(0.6f),
                        color = colorResource(id = R.color.icon_color)
                    )
                }
            }
        },
        bottomBar = {
            TextField(value = message, onValueChange = { message = it },
                placeholder = { Text(text = stringResource(id = R.string.message1),
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
                leadingIcon = {
                    IconButton(onClick = { /*Вложения*/ }) {
                        Icon(painterResource(id = R.drawable.attach), null,
                            modifier = Modifier
                                .padding(start = 7.dp)
                                .size(30.dp))
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { /*Отправить сообщение*/ }) {
                        Icon(painterResource(id = R.drawable.send2), null,
                            modifier = Modifier
                                .padding(end = 7.dp)
                                .size(28.dp))
                    }
                }
                )
        }
    ) {
        Column {
            Divider(thickness = 2.dp, color = Color.Black)
//            LazyColumn(content = )
        }
    }
}