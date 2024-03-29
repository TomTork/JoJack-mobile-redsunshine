package ru.anotherworld.jojack.elements

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.substring
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.anotherworld.jojack.ChatTwo
import ru.anotherworld.jojack.Cipher
import ru.anotherworld.jojack.DataKeys
import ru.anotherworld.jojack.DataMessengerEncrypted
import ru.anotherworld.jojack.EncChatController
import ru.anotherworld.jojack.MImage
import ru.anotherworld.jojack.MainApp
import ru.anotherworld.jojack.R
import ru.anotherworld.jojack.RSAKotlin
import ru.anotherworld.jojack.TMessage
import ru.anotherworld.jojack.TMessage2
import ru.anotherworld.jojack.chatcontroller.Message
import ru.anotherworld.jojack.chatcontroller.getCurrentTimeStamp
import ru.anotherworld.jojack.cipher
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.interFamily
import ru.anotherworld.jojack.login
import ru.anotherworld.jojack.mDatabase
import ru.anotherworld.jojack.nunitoFamily
import ru.anotherworld.jojack.ui.theme.JoJackTheme
import ru.anotherworld.jojack.sDatabase
import java.io.File
import kotlin.concurrent.thread

var idChat2: String = ""
var nameChat2: String = ""
var iconChat2: String = ""
var repost: String = ""
var encChat: Boolean = false
var inviteUrl: String = ""

class Chat2 : ComponentActivity() {
    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    override fun onStop() {
        job.start()
        ioScope.launch {
            if(destroy != null) destroy!!.closeSession()
            else if(destroy2 != null) destroy2!!.closeSession()
        }
        job.cancel()
        super.onStop()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Chat2(
                    idChat2, iconChat2, nameChat2, encChat, inviteUrl
                )
            }
        }
    }
}

private var destroy: ChatTwo? = null
private var destroy2: EncChatController? = null
private var login1: String = ""

val invalidMessagesList = listOf("ls://", "null", "[|start|]")

fun checkMessageInInvalidMessagesList(message: String, invalidList: List<String>): Boolean{
    for(invalid in invalidList){
        if(invalid in message.lowercase()) return true
    }
    return false
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat2(idChat: String, iconChat: String?, nameChat: String,
          encChat: Boolean = false, inviteUrl: String = ""){
    val context = LocalContext.current
    val messagesList = remember { listOf<TMessage2>().toMutableStateList() }
    val messagesList2 = remember { listOf<DataMessengerEncrypted>().toMutableStateList() }
    var state by remember { mutableStateOf("Онлайн") } //Состояние в чате: кто-то печатает и т.д.
    var message by remember { mutableStateOf("") }
    var privateKey by remember { mutableStateOf("") }
    val coroutine = rememberCoroutineScope()
    val smartResult = remember { mutableStateOf<DataMessengerEncrypted?>(null) }
    var _nameChat by remember { mutableStateOf(nameChat) }
    var access by remember { mutableStateOf(false) }
    val chatController = ChatTwo(idChat)
    val encChatController = EncChatController(idChat)
    val mImage = MImage()
    var nameImage by remember{ mutableStateOf("") }
    val users = mutableListOf<DataKeys>()
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        coroutine.launch {
            if(uri != null){
                val name = mImage.uploadImage("in_chat", context.contentResolver.openInputStream(uri)?.readBytes())
                if(name == null){
                    Toast.makeText(context, R.string.error_in_get_image, Toast.LENGTH_SHORT).show()
                }
                else nameImage = name
            }

        }

    }
    coroutine.launch {
        login1 = sDatabase.getLogin()!!
        privateKey = sDatabase.getClosedKey()!!
    }
    var expanded by remember { mutableStateOf(false) }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val lazyState = rememberLazyListState()
    Scaffold(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .background(color = colorResource(id = R.color.background2)),
        topBar = {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .background(color = colorResource(id = R.color.background2))
                .clickable {
                    if ("mchat" in idChat) expanded = !expanded
                }) {
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
                    if(expanded && access){
                        TextField(value = _nameChat, onValueChange = {
                            _nameChat = it
                            //Переименование на стороне сервера
                        })
                    }
                    else{
                        Text(text = _nameChat, fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                            fontSize = 23.sp, color = colorResource(id = R.color.white)
                        )
                    }
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
                    IconButton(onClick = {
                        //Открыть вложения
                        launcher.launch("image/*")
                    }) {
                        Icon(
                            painterResource(id = R.drawable.attach), null,
                            modifier = Modifier
                                .padding(start = 7.dp)
                                .size(30.dp))
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        coroutine.launch { //Отправка сообщения с картинкой
                            if(nameImage != ""){
                                if(!encChat){
                                    messagesList.add(TMessage2(
                                        id = 0,
                                        author = login1,
                                        message = "ls://$nameImage;$message",
                                        timestamp = System.currentTimeMillis()
                                    ))
                                    chatController.sendMessage(
                                        TMessage2(
                                            id = 0,
                                            author = login1,
                                            message = "ls://$nameImage;$message",
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                                else{
                                    val count = encChatController.getCountMessages() ?: 1
                                    val blacklist = encChatController.eChatGetBlacklist()
                                    val time = System.currentTimeMillis()
                                    if(blacklist.isEmpty() || login1 !in blacklist){
                                        messagesList2.add(DataMessengerEncrypted(count, login1, "ls://$nameImage;$message", time, login1))
                                        for(user in users){
                                            if(blacklist.isEmpty() || user.login !in blacklist){
                                                Log.d("SEND-MESSAGE", "${user.login} ${login1}")
                                                encChatController.sendMessage(DataMessengerEncrypted(
                                                    id = count,
                                                    author = login1,
                                                    encText = RSAKotlin.encryptMessage("ls://$nameImage;$message", user.publicKey),
                                                    time = time,
                                                    sendTo = user.login
                                                ))
                                            }
                                        }
                                    }
                                    else Toast.makeText(context, context.getText(R.string.u_in_bl), Toast.LENGTH_SHORT)
                                        .show()
                                }
                                message = ""
                                nameImage = ""
                            }
                        }

                        if(!checkMessageInInvalidMessagesList(message, invalidMessagesList)){
                            coroutine.launch {
                                if(!encChat){
                                    messagesList.add(TMessage2(
                                        id = 0,
                                        author = login1,
                                        message = message,
                                        timestamp = System.currentTimeMillis()
                                    ))
                                    chatController.sendMessage(
                                        TMessage2(
                                            id = 0,
                                            author = login1,
                                            message = message,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                                else{
                                    val count = encChatController.getCountMessages() ?: 1
                                    val blacklist = encChatController.eChatGetBlacklist()
                                    val time = System.currentTimeMillis()
                                    if(blacklist.isEmpty() || login1 !in blacklist){
                                        messagesList2.add(DataMessengerEncrypted(count, login1, message, time, login1))
                                        for(user in users){
                                            if(blacklist.isEmpty() || user.login !in blacklist){
                                                Log.d("SEND-MESSAGE", "${user.login} ${login1}")
                                                encChatController.sendMessage(DataMessengerEncrypted(
                                                    id = count,
                                                    author = login1,
                                                    encText = RSAKotlin.encryptMessage(message, user.publicKey),
                                                    time = time,
                                                    sendTo = user.login
                                                ))
                                            }
                                        }
                                    }
                                    else Toast.makeText(context, context.getText(R.string.u_in_bl), Toast.LENGTH_SHORT)
                                        .show()
                                }

                                message = ""
                            }
                        }
                        else Toast.makeText(context, context.getText(R.string.invalid_input), Toast.LENGTH_SHORT).show()

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
            if(!expanded){
                var ready by remember { mutableStateOf(false) }
                Thread(Runnable {
                    coroutine.launch {
                        if(!encChat){
                            if(!ready){
                                chatController.initSession(sDatabase.getToken()!!)
                                if(repost != ""){
                                    messagesList.add(TMessage2(
                                        id = 0,
                                        author = login1,
                                        message = "[|START|]${repost}[|END|]",
                                        timestamp = System.currentTimeMillis()
                                    ))
                                    chatController.sendMessage(TMessage2(
                                        id = 0,
                                        author = login1,
                                        message = "[|START|]${repost}[|END|]",
                                        timestamp = System.currentTimeMillis()
                                    ))
                                    repost = ""
                                }

                                destroy = chatController
                                val countMessages = chatController.getCountMessages()
                                messagesList.addAll(chatController.getRangeMessages(1, countMessages!!).toMutableStateList())
                            }
                            ready = true

                            val result = chatController.waitNewData()
                            if(result != null){
                                messagesList.add(0, result)
                            }
                        }
                        else{
                            if(!ready){
                                encChatController.initUser()
                                encChatController.initSession(sDatabase.getToken()!!)
                                access = encChatController.eChatGetAccess()
                                users.addAll(encChatController.getAllUsers()?.toMutableStateList() ?: listOf())
                                Log.d("STAGE", users.toList().toString())

                                if(repost != ""){
                                    val count = encChatController.getCountMessages() ?: 1
                                    val blacklist = encChatController.eChatGetBlacklist()
                                    val time = System.currentTimeMillis()
                                    if(blacklist.isEmpty() || login1 !in blacklist){
                                        messagesList2.add(DataMessengerEncrypted(count, login1, "[|START|]${repost}[|END|]", time, login1))
                                        for(user in users){
                                            if(blacklist.isEmpty() || user.login !in blacklist){
                                                encChatController.sendMessage(DataMessengerEncrypted(
                                                    id = count,
                                                    author = login.value,
                                                    encText = RSAKotlin
                                                        .encryptMessage("[|START|]${repost}[|END|]",
                                                            user.publicKey),
                                                    time = time,
                                                    sendTo = user.login
                                                ))
                                            }

                                        }
                                    }
                                    else Toast.makeText(context, context.getText(R.string.u_in_bl), Toast.LENGTH_SHORT)
                                        .show()

                                    repost = ""
                                }

                                destroy2 = encChatController
                                val countMessages = encChatController.getCountMessages()
                                if(countMessages != null){
                                    val blacklist = encChatController.eChatGetBlacklist()
                                    if (login1 !in blacklist){
                                        messagesList2.addAll(encChatController.getRangeMessages(0, countMessages-1))
                                        for(index in messagesList2.indices){
                                            try{
                                                if(messagesList2[index].sendTo == login1){
                                                    messagesList2[index] = DataMessengerEncrypted(
                                                        id = messagesList2[index].id,
                                                        author = messagesList2[index].author,
                                                        encText = RSAKotlin.decryptMessage(messagesList2[index].encText,
                                                            privateKey),
                                                        time = messagesList2[index].time,
                                                        sendTo = messagesList2[index].sendTo
                                                    )
                                                }

                                            } catch (e: Exception){
                                                println("DECODE: $e")
                                            }

                                        }
                                    }

                                }
                            }
                            ready = true

                            smartResult.value = encChatController.waitNewData(privateKey, login1)
                            if(smartResult.value != null){
                                Log.e("WATCH-THIS-2", smartResult.value.toString())
                                messagesList2.add(0, smartResult.value!!)
                            }
                            else Log.d("STATUS", "NULL")
                        }
                    }
                }).start()

                if (ready){
                    HorizontalDivider(thickness = 2.dp, color = Color.Black)
                    //DEPRECATED
//                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
//                    modifier = Modifier
//                        .padding(bottom = 30.dp, start = 5.dp, end = 5.dp)
//                        .align(Alignment.CenterHorizontally)) {
//                    Column(modifier = Modifier
//                        .fillMaxWidth(1f)
//                        .padding(start = 20.dp, end = 20.dp)
//                        .background(color = colorResource(id = R.color.background_post))) {
//                        if("echat" !in idChat && idChat.substring(0, 4) != "chat") {
//                            Row(modifier = Modifier
//                                .fillMaxWidth(1f)
//                                .padding(start = 5.dp, end = 5.dp)
//                                .clickable {
//                                    clipboardManager.setText(AnnotatedString(inviteUrl))
//                                }) {
//                                Text(text = stringResource(id = R.string.copy_invite),
//                                    fontFamily = interFamily, fontWeight = FontWeight.W500)
//                                Text(
//                                    text = inviteUrl,
//                                    modifier = Modifier
//                                        .align(Alignment.CenterVertically)
//                                        .clickable {
//                                            clipboardManager.setText(AnnotatedString(inviteUrl))
//                                        },
//                                    fontFamily = interFamily,
//                                    fontWeight = FontWeight.W400
//                                )
//                            }
//                            val state2 = rememberLazyListState()
//                            var localBlacklist = remember { listOf<String>().toMutableStateList() }
//                            coroutine.launch {
//                                localBlacklist = encChatController.eChatGetBlacklist() as SnapshotStateList<String>
//                            }
//                            LazyColumn(state = state2) {
//                                itemsIndexed(users.map { it.login }.toList()){ index, user ->
//                                    Row(modifier = Modifier.fillMaxWidth(0.8f)){
//                                       Text(text = index.toString(),
//                                           fontFamily = nunitoFamily, fontWeight = FontWeight.W400,
//                                           modifier = Modifier.weight(0.1f))
//                                       Text(text = user,
//                                           modifier = Modifier.weight(0.8f),
//                                           fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
//                                           style = if(user in localBlacklist) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
//                                       )
//                                       if(access){
//                                           IconButton(onClick = {
//                                               if(user in localBlacklist){
//                                                   coroutine.launch {
//                                                       encChatController.eChatRemoveFromBlacklist(user)
//                                                   }
//                                               }
//                                               else{
//                                                   coroutine.launch {
//                                                       encChatController.eChatAddInBlacklist(user)
//                                                   }
//                                               }
//                                           },
//                                               modifier = Modifier.weight(0.1f)) {
//                                               if(user in localBlacklist){
//                                                   Icon(painterResource(id = R.drawable.outline_cancel_24), null)
//                                               }
//                                               else Icon(painterResource(id = R.drawable.outline_check_24), null)
//                                           }
//                                       }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
                    if(!encChat){
//                    Log.d("LIST", messagesList.toList().toString())
                        LazyColumn(state = lazyState, reverseLayout = true){
                            itemsIndexed(messagesList.reversed()){ _, message ->
                                MessageIn(
                                    login = message.author,
                                    text = if("ls://" in message.message) message.message.substringAfter(";") else message.message,
                                    time = getCurrentTimeStamp(message.timestamp)!!,
                                    urlImage = if("ls://" in message.message) message.message.substringBefore(";") else null)
                                Spacer(modifier = Modifier.padding(top = 5.dp))
                            }
                        }
                    }
                    else{
                        Log.d("LIST", messagesList2.toList().toString())
                        LazyColumn(state = lazyState, reverseLayout = true) {
                            itemsIndexed(messagesList2.reversed()){ _, message ->
                                if(("/" !in message.encText && "=" !in message.encText && "+" !in message.encText) || "ls://" in message.encText){
                                    MessageIn(
                                        login = message.author,
                                        text = if("ls://" in message.encText) message.encText.substringAfter(";") else message.encText,
                                        time = getCurrentTimeStamp(message.time)!!,
                                        urlImage = if("ls://" in message.encText) message.encText.substringBefore(";") else null)
                                    Spacer(modifier = Modifier.padding(top = 5.dp))
                                }
                            }
                        }
                    }

                }
            }
            if(expanded){ //Список пользователей, информация, пригласительная ссылка и т.д.
                Column(modifier = Modifier
                    .fillMaxSize(1f)) {
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
                            .align(Alignment.CenterHorizontally)
                    )
                    if(inviteUrl != ""){
                        Text(text = "${context.getText(R.string.invite_url)}$inviteUrl",
                            fontWeight = FontWeight.W600, fontFamily = nunitoFamily,
                            modifier = Modifier
                                .clickable { clipboardManager.setText(AnnotatedString(inviteUrl)) }
                                .align(Alignment.CenterHorizontally))
                    }
                    HorizontalDivider(thickness = 2.dp, color = Color.Black)
                    val state2 = rememberLazyListState()
                    var localBlacklist = remember { listOf<String>().toMutableStateList() }
                    coroutine.launch {
                        localBlacklist = encChatController.eChatGetBlacklist() as SnapshotStateList<String>
                    }
                    if("echat" in idChat || "emchat" in idChat) {
                        LazyColumn(state = state2) {
                            itemsIndexed(users.map { it.login }.toList()) { index, user ->
                                Row(modifier = Modifier.fillMaxWidth(0.8f)) {
                                    Text(
                                        text = index.toString(),
                                        fontFamily = nunitoFamily, fontWeight = FontWeight.W400,
                                        modifier = Modifier.weight(0.1f)
                                    )
                                    Text(
                                        text = user,
                                        modifier = Modifier.weight(0.8f),
                                        fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                                        style = if (user in localBlacklist) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                                    )
                                    if (access) {
                                        IconButton(
                                            onClick = {
                                                if (user in localBlacklist) {
                                                    coroutine.launch {
                                                        encChatController.eChatRemoveFromBlacklist(
                                                            user
                                                        )
                                                    }
                                                } else {
                                                    coroutine.launch {
                                                        encChatController.eChatAddInBlacklist(user)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(0.1f)
                                        ) {
                                            if (user in localBlacklist) {
                                                Icon(
                                                    painterResource(id = R.drawable.outline_cancel_24),
                                                    null
                                                )
                                            } else Icon(
                                                painterResource(id = R.drawable.outline_check_24),
                                                null
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }

                }

            }


        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun MessageIn(login: String, text: String, time: String, urlImage: String? = null){
    val coroutine = rememberCoroutineScope()
    val mImage = MImage()
    val login1 by remember { mutableStateOf(login1) }
    var eq by remember { mutableStateOf(false) }
    val imageFile = remember { mutableStateOf<File?>(null) }
    eq = (login1 == login) //true if user you
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
            if(!eq){
                Text(text = login, fontFamily = nunitoFamily, fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(end = 10.dp, start = 10.dp))

            }
            if("[|START|]" in text && "[|END|]" in text){
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
            else if(urlImage != null){
                coroutine.launch {
                    imageFile.value = mImage.getImage(urlImage)
                }
                if(imageFile.value != null){
                    AsyncImage(model = BitmapFactory.decodeFile(imageFile.value?.absolutePath),
                        contentDescription = null, modifier = Modifier
                            .size(400.dp)
                            .align(Alignment.CenterHorizontally))
                }
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