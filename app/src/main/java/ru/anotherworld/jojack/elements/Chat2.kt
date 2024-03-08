package ru.anotherworld.jojack.elements

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.AbsoluteAlignment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.anotherworld.jojack.ChatController
import ru.anotherworld.jojack.ChatTwo
import ru.anotherworld.jojack.Cipher
import ru.anotherworld.jojack.MainApp
import ru.anotherworld.jojack.R
import ru.anotherworld.jojack.TMessage
import ru.anotherworld.jojack.TMessage2
import ru.anotherworld.jojack.chatcontroller.Message
import ru.anotherworld.jojack.chatcontroller.getCurrentTimeStamp
import ru.anotherworld.jojack.cipher
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.mDatabase
import ru.anotherworld.jojack.ui.theme.JoJackTheme
import ru.anotherworld.jojack.sDatabase

var idChat2: String = ""
var nameChat2: String = ""
var iconChat2: String = ""
var repost: String = ""

class Chat2 : ComponentActivity() {
    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch {
            destroy!!.closeSession()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Chat2(
                    idChat2, iconChat2, nameChat2
                )
            }
        }
    }
}

private var destroy: ChatTwo? = null
private var login1: String = ""

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat2(idChat: String, iconChat: String?, nameChat: String){
    val context = LocalContext.current
    val nunitoFamily = FontFamily(Font(R.font.nunito_medium500, FontWeight.W500))
    var state by remember { mutableStateOf("Онлайн") } //Состояние в чате: кто-то печатает и т.д.
    var message by remember { mutableStateOf("") }
    val coroutine = rememberCoroutineScope()
    val chatController = ChatTwo(idChat)
    val database = MainDatabase()
    coroutine.launch {
        login1 = sDatabase.getLogin()!!
    }
    val lazyState = rememberLazyListState()
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
                    Icon(
                        painterResource(id = R.drawable.arrow_back), null,
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
                        fontSize = 23.sp, color = colorResource(id = R.color.white)
                    )
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
                        .fillMaxWidth(1f)
                        .padding(start = 3.dp)
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.white),
                    fontSize = 20.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(id = R.color.background_field),
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,

                    ),
                modifier = Modifier.fillMaxWidth(1f),
                leadingIcon = {
                    IconButton(onClick = { /*Вложения*/ }) {
                        Icon(
                            painterResource(id = R.drawable.attach), null,
                            modifier = Modifier
                                .padding(start = 7.dp)
                                .size(30.dp))
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        coroutine.launch {
                            chatController.sendMessage(
                                TMessage2(
                                id = 0,
                                author = database.getLogin()!!,
                                message = message,
                                timestamp = System.currentTimeMillis()
                            )
                            )
                            message = ""
                        }
                    }) {
                        Icon(
                            painterResource(id = R.drawable.send2), null,
                            modifier = Modifier
                                .padding(end = 7.dp)
                                .size(28.dp))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(top = 60.dp, bottom = 60.dp)
        ) {
            val messagesList = remember { listOf<TMessage2>().toMutableStateList() }
            var ready by remember { mutableStateOf(false) }
            coroutine.launch {
                if(!ready){
                    chatController.initSession(sDatabase.getToken()!!)

                    if(repost != ""){
                        chatController.sendMessage(TMessage2(
                            id = 0,
                            author = sDatabase.getLogin()!!,
                            message = "[|START|]${repost}[|END|]",
                            timestamp = System.currentTimeMillis()
                        ))
                        repost = ""
                    }

                    destroy = chatController
                    val countMessages = chatController.getCountMessages()
                    Log.d("INFO", countMessages.toString())
                    messagesList.addAll(chatController.getRangeMessages(1, countMessages!!).toMutableStateList())
                    Log.d("INFO4", messagesList.toList().toString())

                }
                ready = true

                val result = chatController.waitNewData()
                if(result != null){
                    messagesList.add(0, result)
                }

            }
            if (ready){
                Divider(thickness = 2.dp, color = Color.Black)
                LazyColumn(state = lazyState,
                    reverseLayout = true){
                    itemsIndexed(messagesList.sortedBy { it.timestamp }.reversed()){ _, message ->
                        MessageIn(
                            login = message.author,
                            text = message.message,
                            time = getCurrentTimeStamp(message.timestamp)!!)
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun MessageIn(login: String, text: String, time: String){
    val coroutine = rememberCoroutineScope()
    val login1 by remember { mutableStateOf(login1) }
    var eq by remember { mutableStateOf(false) }
    eq = (login1 == login) //true if user you
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_medium500, FontWeight.W500),
        Font(R.font.nunito_light400, FontWeight.W400)
    )
    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 1.dp, bottom = 1.dp)
            .fillMaxWidth(1f),
        horizontalArrangement = if (eq) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (eq) colorResource(id = R.color.my_message_color) else colorResource(id = R.color.message_color),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .widthIn(min = 100.dp),
            horizontalAlignment = if (eq) AbsoluteAlignment.Right else AbsoluteAlignment.Left
        ) {
            if("[|START|]" in text && "[|END|]" in text){
                Log.d("DATA", text.substringAfter("[|START|]").substringBefore("[|END|]"))
                val data = Json.decodeFromString<CopyPost>(
                    text.substringAfter("[|START|]").substringBefore("[|END|]"))
                PostBase3(
                    idPost = data.idPost,
                    text = data.text,
                    nameGroup = data.nameGroup,
                    iconGroup = data.iconGroup,
                    typeGroup = data.typeGroup,
                    images = data.images,
                    originalUrl = data.originalUrl,
                    like = data.like,
                    exclusive = data.exclusive,
                    myMessage = eq
                )
            }
            else {
                Text(text = text, fontFamily = nunitoFamily, fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(end = 10.dp, start = 10.dp))
            }
            Text(text = time, fontFamily = nunitoFamily, fontWeight = FontWeight.W400,
                modifier = Modifier
                    .padding(end = 12.dp, start = 12.dp)
                    .alpha(0.5f),
                fontSize = 10.sp)

        }
    }
}