package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.anotherworld.jojack.database.ChatsData
import ru.anotherworld.jojack.database.ChatsDatabase
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.elements.Chat2
import ru.anotherworld.jojack.elements.ChatActivity
import ru.anotherworld.jojack.elements.ChatMessage
import ru.anotherworld.jojack.elements.CopyPost
import ru.anotherworld.jojack.elements.PostBase2
import ru.anotherworld.jojack.elements.encChat
import ru.anotherworld.jojack.elements.iconChat2
import ru.anotherworld.jojack.elements.idChat2
import ru.anotherworld.jojack.elements.nameChat2
import ru.anotherworld.jojack.ui.theme.JoJackTheme
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.NoRouteToHostException

var login = mutableStateOf("")
var id = mutableIntStateOf(-1)
var job = mutableIntStateOf(-1)

class MainApp : ComponentActivity() {
    @SuppressLint("PermissionLaunchedDuringComposition", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                val coroutine = rememberCoroutineScope()
                val context = LocalContext.current
                var start by remember { mutableStateOf(false) }
                try{ //Check first enter or no

                    coroutine.launch {
                        val localLogin = mDatabase.getLogin()
                        if(localLogin == null || localLogin == ""){
                            start = false
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                        else {
                            start = true
                            login.value = localLogin
                            id.intValue = mDatabase.getServerId()!!
                            job.intValue = mDatabase.getJob()!!
                        }
                    }
                } catch (io: Exception){
                    start = false
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
                if(start){
                    MissingPermissionsComponent {
                        Content()
                    }
                }
            }
        }
    }
}

val chatsDatabase = ChatsDatabase()

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MissingPermissionsComponent(
    content: @Composable () -> Unit
) { //Activity-check user's permissions
    val context = LocalContext.current
    var noPerm by remember { mutableStateOf(false) }
    var permissions = listOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions = permissions.plus(
            listOf(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        )
    }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissions,
    )
    if (permissionsState.allPermissionsGranted || noPerm || (Build.VERSION.SDK_INT >= 33 && rememberPermissionState(
            permission = android.Manifest.permission.POST_NOTIFICATIONS).status.isGranted)) {
        content()
    }
    else {
        val nunitoFamily = FontFamily(
            Font(R.font.inter_medium500, FontWeight.W500),
            Font(R.font.inter600, FontWeight.W600)
        )
        Column(modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .background(color = colorResource(id = R.color.background2))) {
            Column(modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .weight(0.95f)) {
                Text(text = stringResource(id = R.string.not_yet), fontSize = 30.sp,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                    modifier= Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 10.dp, end = 10.dp, top = 30.dp),
                    color = colorResource(id = R.color.white))
                Spacer(modifier = Modifier.weight(0.5f))
                ElevatedButton(onClick = { permissionsState.launchMultiplePermissionRequest(); noPerm = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.background_lr_button)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 30.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.9f)) {
                    Text(text = stringResource(id = R.string.yet), fontFamily = nunitoFamily,
                        fontWeight = FontWeight.W500, modifier = Modifier,
                        color = colorResource(id = R.color.white))
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }
            Text(text = stringResource(id = R.string.no_permissions),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.05f)
                    .clickable { noPerm = true },
                color = colorResource(id = R.color.background_lr_text),
                fontSize = 14.sp)
        }

    }
}

val mDatabase = MainDatabase()
val getInfo = GetInfo()
val getPostVk = GetPostVk()
var contentManager = mutableIntStateOf(0)
var showBars = mutableStateOf(true)
val nunitoFamily = FontFamily(
    Font(R.font.nunito_semibold600, FontWeight.W600),
    Font(R.font.nunito_light400, FontWeight.W400),
    Font(R.font.nunito_medium500, FontWeight.W500)
)
val interFamily = FontFamily(
    Font(R.font.inter600, FontWeight.W600),
    Font(R.font.inter_medium500, FontWeight.W500)
)


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "UnrememberedMutableState"
)
@Composable
private fun Content(){ //Main Activity
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    var showSearchUser by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var searchUpdate by remember { mutableStateOf(false) }
    var searchExample: SearchP? = null
    val searchF = SearchIdOrName()
    Scaffold(
        modifier = Modifier.background(Color.White),
        topBar = {
            if (!showSearchUser){
                showBars.value = true
                var topText by mutableStateOf(stringResource(id = R.string.home))
                topText = when(contentManager.intValue){
                    1 -> stringResource(id = R.string.message)
                    2 -> stringResource(id = R.string.settings)
                    else -> stringResource(id = R.string.home)
                }
                Surface(modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth(1f)) {
                    Column(modifier = Modifier.fillMaxWidth(1f)) {
                        Row(modifier = Modifier
                            .fillMaxWidth(1f)
                            .background(colorResource(id = R.color.background2))) {
                            Image(painterResource(id = R.drawable.jojacks_fixed_optimized),
                                null, modifier= Modifier
                                    .size(65.dp)
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 20.dp, end = 5.dp))
                            Text(text = stringResource(id = R.string.app_name),
                                fontFamily = interFamily, fontWeight = FontWeight.W600,
                                fontSize = 30.sp, modifier = Modifier
                                    .align(Alignment.CenterVertically))
                            Spacer(modifier = Modifier.padding(bottom=15.dp))
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Absolute.Right,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .fillMaxWidth(1f)
                                    .padding(end = 20.dp)) {
                                if(contentManager.intValue == 0) {
                                    IconButton(onClick = {
                                        context.startActivity(Intent(context, NotificationActivity::class.java))
                                    }) {
                                        Column(modifier = Modifier
                                            .padding(end = 15.dp)
                                            .align(Alignment.CenterVertically)
                                            .offset(y = 3.dp)) {
                                            Image(painterResource(id = R.drawable.notificate1),
                                                null,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .size(25.dp))
                                            Image(painterResource(id = R.drawable.notificate2),
                                                null,
                                                modifier = Modifier
                                                    .align(Alignment.CenterHorizontally)
                                                    .size(9.dp)
                                                    .offset(y = (-3).dp))
                                        }
                                    }
                                }
                                else if(contentManager.intValue == 1){
                                    IconButton(onClick = { //Search new user -> show search-activity
                                        showSearchUser = !showSearchUser
                                    }) {
                                        Image(painterResource(id = R.drawable.search2),
                                            null, modifier = Modifier.size(25.dp))
                                    }
                                }

                            }
                        }

                    }

                }
            }
            else {
                showBars.value = false
                TopAppBar(title = {
                    Row(modifier = Modifier
                        .fillMaxWidth(1f)
                        .background(colorResource(id = R.color.background2))) {
                        IconButton(onClick = { showSearchUser = false },
                            modifier = Modifier.align(Alignment.CenterVertically)) {
                            Icon(painterResource(id = R.drawable.arrow_back), null,
                                tint = colorResource(id = R.color.white))
                        }
                        TextField(value = search,
                            onValueChange = {
                                search = it
                                coroutine.launch {
                                    searchExample = searchF.search(search)
                                    searchUpdate = true
                                }
                                            },
                            placeholder = { Text(text = stringResource(id = R.string.search_user),
                                fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                                modifier = Modifier.alpha(0.5f),
                                color = colorResource(id = R.color.white)) },
                            maxLines = 1,
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = colorResource(id = R.color.background2),
                                cursorColor = Color.White,
                                disabledLabelColor = colorResource(id = R.color.ghost_white),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                ))
                    }
                }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
                    colors = TopAppBarDefaults
                        .topAppBarColors(containerColor = colorResource(id = R.color.background2),
                            titleContentColor = colorResource(id = R.color.background2)))
            }

        },
        bottomBar = {
            if(showBars.value){
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .background(colorResource(id = R.color.background2)),
                    containerColor = colorResource(id = R.color.background2),
                    contentColor = colorResource(id = R.color.background2),
                    contentPadding = PaddingValues(bottom = 10.dp),

                    ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .background(colorResource(id = R.color.black2))
                    ) {
                        IconButton(onClick = { contentManager.intValue = 0 },
                            modifier = Modifier
                                .weight(0.33f)
                                .align(Alignment.CenterVertically)
                                .background(colorResource(id = R.color.background2))) {
                            Column {
                                Icon(painterResource(id = R.drawable.home21), null,
                                    tint = if(contentManager.intValue == 0) colorResource(id = R.color.white) else colorResource(id = R.color.text_color),
                                    modifier = Modifier.align(Alignment.CenterHorizontally))
                                Icon(painterResource(id = R.drawable.home22), null,
                                        tint = if(contentManager.intValue == 0) colorResource(id = R.color.white) else colorResource(id = R.color.text_color),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .offset(y = (-6).dp)
                                            .scale(scaleX = 1.5f, scaleY = 1f))
                                Text(text = stringResource(id = R.string.home),
                                    Modifier
                                        .clickable { contentManager.intValue = 0 }
                                        .align(Alignment.CenterHorizontally),
                                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                                    color = if(contentManager.intValue == 0) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                            }
                        }
                        IconButton(onClick = { contentManager.intValue = 1 },
                            modifier = Modifier
                                .weight(0.33f)
                                .align(Alignment.CenterVertically)
                                .background(colorResource(id = R.color.background2))) {
                            Column {
                                Icon(painterResource(id = R.drawable.comments2), null,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    tint = if(contentManager.intValue == 1) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                                Text(text = stringResource(id = R.string.message),
                                    Modifier
                                        .clickable { contentManager.intValue = 1 }
                                        .align(Alignment.CenterHorizontally),
                                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                                    color = if(contentManager.intValue == 1) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                            }

                        }
                        IconButton(onClick = { contentManager.intValue = 2 },
                            modifier = Modifier
                                .weight(0.33f)
                                .align(Alignment.CenterVertically)
                                .background(colorResource(id = R.color.background2))) {
                            Column {
                                Icon(painterResource(id = R.drawable.settings2), null,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    tint = if(contentManager.intValue == 2) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                                Text(text = stringResource(id = R.string.settings),
                                    Modifier
                                        .clickable { contentManager.intValue = 2 }
                                        .align(Alignment.CenterHorizontally),
                                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                                    color = if(contentManager.intValue == 2) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                            }
                        }
                    }
                }
            }

        }
    ) {
        if (showSearchUser){ //Отобразить поиск

            var checkedSwitch by remember { mutableStateOf(false) }
            var checkedSwitch2 by remember { mutableStateOf(false) }
            val arrayPeopleIds = remember { listOf<String>().toMutableStateList() }
            var myId by remember { mutableIntStateOf(-1) }

            coroutine.launch {
                myId = mDatabase.getServerId()!!
            }

            Column(modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .animateContentSize()
                .background(colorResource(id = R.color.black2))
                .padding(top = 65.dp)) {

                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 20.dp, end = 20.dp, bottom = 5.dp, top = 5.dp)) {
                    Text(text = stringResource(id = R.string.group),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .weight(0.9f)
                            .align(Alignment.CenterVertically))
                    Switch(checked = checkedSwitch,
                        onCheckedChange = { if (!checkedSwitch2) checkedSwitch = !checkedSwitch },
                        modifier = Modifier
                            .weight(0.1f)
                            .align(Alignment.CenterVertically),
                        colors = SwitchDefaults
                            .colors(checkedThumbColor = colorResource(id = R.color.subscribe)))
                }
                HorizontalDivider(thickness = 2.dp, color = colorResource(id = R.color.text_color),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .alpha(0.5f))
                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 20.dp, end = 20.dp, bottom = 5.dp, top = 5.dp)) {
                    Text(text = stringResource(id = R.string.enable_encrypt),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .weight(0.9f)
                            .align(Alignment.CenterVertically))
                    Switch(checked = checkedSwitch2,
                        onCheckedChange = {
                            checkedSwitch2 = !checkedSwitch2
                            if(checkedSwitch) checkedSwitch = false
                        },
                        modifier = Modifier
                            .weight(0.1f)
                            .align(Alignment.CenterVertically),
                        colors = SwitchDefaults
                            .colors(checkedThumbColor = colorResource(id = R.color.subscribe)))
                }
                HorizontalDivider(thickness = 2.dp, color = colorResource(id = R.color.text_color),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .alpha(0.5f))
                LazyColumn(modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(0.9f)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    state = rememberLazyListState()){
                    if(searchUpdate){
                        searchUpdate = false
                        coroutine.launch {
                            itemsIndexed(searchExample!!.arr){ index, item ->
                                var chBox by remember { mutableStateOf(false) }
                                Row(modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .clickable {
                                        if (!checkedSwitch) {
                                            if (checkedSwitch2) { //chat with encryption
                                                val number = arrayOf(myId.toString(), item.second)
                                                number.sort()
                                                idChat2 = "echat" + number.joinToString("x")
                                                nameChat2 = item.first
                                                iconChat2 = ""

                                                coroutine.launch {
                                                    chatsDatabase.insertAll(
                                                        ChatsData(
                                                            idChat2,
                                                            nameChat2,
                                                            item.first,
                                                            ""
                                                        )
                                                    )
                                                }
                                                encChat = true
                                                context.startActivity(
                                                    Intent(
                                                        context,
                                                        Chat2::class.java
                                                    )
                                                )
                                            } else { //chat without encryption
                                                val number = arrayOf(myId.toString(), item.second)
                                                number.sort()
                                                idChat2 = "chat" + number.joinToString("x")
                                                nameChat2 = item.first
                                                iconChat2 = ""
                                                encChat = false

                                                coroutine.launch {
                                                    chatsDatabase.insertAll(
                                                        ChatsData(
                                                            idChat2,
                                                            nameChat2,
                                                            item.first,
                                                            ""
                                                        )
                                                    )
                                                }

                                                context.startActivity(
                                                    Intent(
                                                        context,
                                                        Chat2::class.java
                                                    )
                                                )
                                            }
                                        } else chBox = !chBox
                                    }
                                    .background(
                                        color = colorResource(id = R.color.background_field),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 5.dp,
                                        end = 5.dp
                                    )) {
                                    Column(modifier = Modifier.weight(0.9f)) {
                                        Text(text = stringResource(id = R.string.login) + ": " + item.first,
                                            fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                                            modifier = Modifier.padding(start = 10.dp))
                                        Text(text = stringResource(id = R.string.ID) + " " + item.second,
                                            fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                                            modifier = Modifier.padding(start = 10.dp))
                                    }
                                    if (checkedSwitch){
                                        Checkbox(checked = chBox, onCheckedChange = {
                                            chBox = !chBox
                                            if(chBox) arrayPeopleIds.add(item.second)
                                            else if(item.second in arrayPeopleIds) arrayPeopleIds.remove(item.second) },
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .weight(0.1f), colors = CheckboxDefaults.colors(
                                                checkedColor = colorResource(id = R.color.subscribe)
                                            )
                                        )
                                    }
                                }
                                HorizontalDivider(modifier = Modifier
                                    .padding(top = 4.dp, bottom = 4.dp)
                                    .alpha(0.3f),
                                    thickness = 2.dp, color = colorResource(id = R.color.white))
                            }
                        }

                    }
                }
                if(checkedSwitch){
                    FloatingActionButton(onClick = {
                        Log.d("ARRAY", arrayPeopleIds.toList().toString())
                    }, modifier = Modifier
                        .weight(0.1f)
                        .offset(x = (-10).dp, y = (-80).dp)
                        .align(Alignment.End)) {
                        Icon(Icons.Filled.Add, null)
                    }
                }
            }

        }
        else{
            when(contentManager.intValue){
                0 -> { NewsPaper() }
                1 -> { Messenger() }
                2 -> { Account() }
            }
        }
    }
}

private data class SNews(
    val textPosts: String,
    val nameGroup: String,
    val icon: String,
    val images: VkImageAndVideo,
    val originalUrl: String,
    val like: Int,
    val exclusive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState",
    "UnrememberedMutableState")
@Composable
private fun NewsPaper(){
    val context = LocalContext.current
    var isServerConnect by remember { mutableStateOf(true) }
    var update by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    var maxId by remember { mutableIntStateOf(1) }
    var view by remember { mutableStateOf(false) }
    var view2 by remember { mutableStateOf(false) }
    val array = remember { listOf<SNews>().toMutableStateList() }
    val listState = rememberLazyListState()
    var job by remember { mutableIntStateOf(0) }
    var startCreateNewPostFromUser by remember { mutableStateOf(false) }
    if(isServerConnect || update){
        if(update){
            update = false
            view2 = false
            view = false
        }

        coroutine.launch {
            try {
                if (!view2) {
                    maxId = getInfo.getMaxId()
                    view2 = true
                }
                if (view2) {
                    if (!view) {
                        maxId -= 10
                        for (i in getPostVk.getPostVk(maxId, maxId + 9).post) {
                            array.add(
                                SNews(
                                    i.textPost, i.groupName, i.iconUrl, i.imagesUrls,
                                    i.originalUrl, i.like, i.exclusive
                                )
                            )
                        }
                        view = true
                    }
                }
                job = sDatabase.getJob()!!
            } catch (e: Exception) {
                isServerConnect = when (e) {
                    is ConnectTimeoutException, is NoRouteToHostException -> false
                    else -> true
                }
            }
        }
    }
    if(!startCreateNewPostFromUser){
        Column (modifier = Modifier
            .padding(top = 50.dp, bottom = if (showBars.value) 60.dp else 0.dp)
            .fillMaxWidth(1f)
            .background(color = colorResource(id = R.color.background2)),
            verticalArrangement = Arrangement.Center) {
            if(view){
                val viewModel = viewModel<MainViewModel>()
                val isLoading by viewModel.isLoading.collectAsState()

                val state = rememberSwipeRefreshState(isRefreshing = isLoading)

                SwipeRefresh(state = state, onRefresh = { viewModel.loadStuff(); update = true },
                    indicator = { _state, refreshTrigger ->
                        SwipeRefreshIndicator(state = _state, refreshTriggerDistance = refreshTrigger,
                            contentColor = colorResource(id = R.color.subscribe))
                    }) {
                    LazyColumn(state = listState){
                        if(job >= 2){ //Функция создания постов доступна не для всех пользователей
                            item { //View listener to create new post from user
                                Row(modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .clickable { startCreateNewPostFromUser = true }
                                    .background(color = colorResource(id = R.color.black))
                                    .padding(
                                        top = 22.dp,
                                        start = 10.dp,
                                        end = 10.dp,
                                        bottom = 10.dp
                                    )
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.account_circle),
                                        null, tint = colorResource(id = R.color.white),
                                        modifier = Modifier.size(37.dp))
                                    Text(text = stringResource(id = R.string.write_something),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .alpha(0.8f)
                                            .padding(start = 10.dp),
                                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                                        fontSize = 18.sp)
                                }
                            }
                        }
                        itemsIndexed(items = array, itemContent = {index, value ->
                            PostBase2(idPost = index, text = value.textPosts, nameGroup = value.nameGroup, typeGroup = "Паблик",
                                iconGroup = value.icon, existsImages = true, images = value.images,
                                originalUrl = value.originalUrl, like = value.like, exclusive = value.exclusive)
                            if(index == array.size - 2 && maxId > 1){
                                coroutine.launch {
                                    maxId -= 1
                                    for(i in getPostVk.getPostVk(maxId, maxId).post)
                                        array.add(SNews(i.textPost, i.groupName, i.iconUrl, i.imagesUrls,
                                            i.originalUrl, i.like, i.exclusive))
                                }
                            }
                        })
                    }
                }
            }
            else if(!isServerConnect){ //show activity-no-internet
                Column(modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f)
                    .background(color = colorResource(id = R.color.background2)),
                    verticalArrangement = Arrangement.Center) {
                    Text(text = stringResource(id = R.string.no_internet2),
                        fontFamily = interFamily, fontWeight = FontWeight.W600,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(start = 20.dp, end = 20.dp),
                        fontSize = 20.sp, color = colorResource(id = R.color.white))
                    Button(onClick = { isServerConnect = true },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.background_lr_button))) {
                        Text(text = stringResource(id = R.string.try_again),
                            fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                            color = colorResource(id = R.color.white))
                    }
                }

            }
        }    
    }
    else { //show create-new-post-activity-from-user
        var textPost by remember { mutableStateOf("") }
        Column(modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .padding(top = 20.dp, bottom = if (showBars.value) 60.dp else 0.dp)
            .background(color = colorResource(id = R.color.background2))) {

            Row(modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(1f)
                .align(Alignment.End)) {
                IconButton(onClick = { startCreateNewPostFromUser = false },
                    modifier = Modifier
                        .padding(start = 10.dp)) {
                    Icon(painter = painterResource(id = R.drawable.clear), contentDescription = null,
                        tint = colorResource(id = R.color.white))
                }
                Spacer(modifier = Modifier.weight(0.8f))
                Button(onClick = { /*Опубликовать пост*/ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent)) {
                    Text(text = stringResource(id = R.string.publish),
                        color = colorResource(id = R.color.white),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600)
                }
            }
            HorizontalDivider(thickness = 2.dp, color = colorResource(id = R.color.white),
                modifier = Modifier
                    .alpha(0.5f)
                    .padding(bottom = 10.dp))
            TextField(value = textPost, onValueChange = { textPost = it },
                placeholder = {
                    Text(text = stringResource(id = R.string.write_something),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 3.dp)
                            .alpha(0.5f),
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.white))
                },
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .onFocusChanged { showBars.value = !it.isFocused },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ))

        }

    }
    
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun Messenger(){
    val context = LocalContext.current
    val array = remember { listOf<ChatsData>().toMutableStateList() }
    val coroutine = rememberCoroutineScope()
    val listenerUpdateChats = remember { mutableStateOf(false) }
    var previewName by remember { mutableStateOf("") }
    var previewMessage by remember { mutableStateOf("") }
    coroutine.launch {
        array.addAll(chatsDatabase.getAll())
    }
    if(listenerUpdateChats.value){
        coroutine.launch {
            array.clear()
            array.addAll(chatsDatabase.getAll())
            listenerUpdateChats.value = false
        }
    }
    Column (modifier = Modifier
        .padding(top = 60.dp, bottom = 60.dp)
        .clip(
            RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        )
        .background(colorResource(id = R.color.background2)), verticalArrangement = Arrangement.Center) {
        LazyColumn{
            item {
                ChatMessage(name = "Флудилка", previewMessage = "1", username = "1", idChat = 0,
                    action = { context.startActivity(Intent(context, ChatActivity::class.java)) },
                    listener = listenerUpdateChats)
            }
            itemsIndexed(array){ index, item ->
                coroutine.launch {
                    try{
                        val chatTwo = ChatTwo(item.chat)
                        val count = chatTwo.getCountMessages()!!
                        val value = chatTwo.getRangeMessages(count, count)
                        if("[|START|]" in value[0].message){
                            val dd = Json.decodeFromString<CopyPost>(value[0].message
                                .substringAfter("[|START|]").substringBefore("[|END|]"))
                            previewMessage = dd.nameGroup + " " + dd.text.substring(0, 16) + "..."
                        } else previewMessage = value[0].message
                        previewName = value[0].author
                    } catch (e: Exception){
                        Log.e("ERROR ::MainApp", e.message.toString())
                    }
                }
                ChatMessage(name = item.name, previewMessage = previewMessage, username = previewName,
                    action = {
                        idChat2 = item.chat
                        nameChat2 = item.name
                        iconChat2 = item.icon
                        if("echat" !in idChat2) context.startActivity(Intent(context, Chat2::class.java))
                        else{
                            encChat = true
                            context.startActivity(Intent(context, Chat2::class.java))
                        }
                    },
                    listener = listenerUpdateChats) //show chats
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
private fun Account(){
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var updateImage by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var privacy by rememberSaveable { mutableStateOf(false) }

    val past = File("data/data/ru.anotherworld.jojack/icon.png")
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(colorResource(id = R.color.black2))
        .padding(top = 70.dp, bottom = 60.dp)) {

        coroutine.launch {
            if(mDatabase.getServerId() == -1){
                val initUser = InitUser()
                val data = initUser.getInit(mDatabase.getLogin()!!, mDatabase.getToken()!!)
                mDatabase.setServerId(data.id)
                mDatabase.setJob(data.job)
                mDatabase.setTrustLevel(data.trustLevel)
            }
            privacy = mDatabase.getPrivacy()!!
            val bitF = mDatabase.getIcon()
            if(bitF != null) imageUri = Uri.parse(bitF.toString())
        }

        var ready by remember { mutableStateOf(false) }
        val mIcon = MIcon()
        val launcher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            if(past.exists()) past.delete()
            createFileFromContentUri(imageUri!!, context).copyTo(past)
            ready = true
            updateImage = true
            coroutine.launch {
                mIcon.uploadImage("112", context.contentResolver.openInputStream(uri!!)?.readBytes())
            }
        }
        var job_ by rememberSaveable { mutableIntStateOf(0) }
        var login_ by rememberSaveable { mutableStateOf("") }
        var serverId_ by rememberSaveable { mutableIntStateOf(0) }
        coroutine.launch {
            job_ = job.intValue
            login_ = login.value
            serverId_ = id.intValue

            if(!past.exists()){
                try {
                    mIcon.getIcon(login_)
                    updateImage = true
                } catch (e: java.lang.IllegalStateException){
                    Log.e("ERROR", e.message.toString())
                }

            }
        }
        val job = when(job_){
            -1 -> stringResource(id = R.string.blocked_user)
            0 -> stringResource(id = R.string.user)
            1 -> stringResource(id = R.string.author)
            2 -> stringResource(id = R.string.moderator)
            3 -> stringResource(id = R.string.editor)
            4 -> stringResource(id = R.string.admin)
            5 -> stringResource(id = R.string.ck_user)
            else -> "ERROR in job-switch ::MainApp"
        }
        Column(modifier = Modifier
            .align(Alignment.CenterHorizontally)) {
            IconButton(onClick = {
                launcher.launch("image/*")
            }, modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)) {
                if(!ready){
                    Icon(painterResource(id = R.drawable.account_circle), null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(10.dp)))
                }
                if(ready || past.exists()){
                    AsyncImage(model = BitmapFactory.decodeFile(past.absolutePath), contentDescription = null,
                        contentScale = ContentScale.Crop)
                }
                if(updateImage){
                    AsyncImage(model = BitmapFactory.decodeFile(past.absolutePath), contentDescription = null,
                        contentScale = ContentScale.Crop)
                    updateImage = false

                }
            }
            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = stringResource(id = R.string.login) + ": " + login_,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W500)
                Text(text = stringResource(id = R.string.job) + " " + job,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W500)
                Text(text = stringResource(id = R.string.ID) + " " + serverId_,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W500)
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Column(Modifier.verticalScroll(scrollState)) {
            Row (modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                .background(
                    colorResource(id = R.color.background2),
                    shape = RoundedCornerShape(14.dp)
                )
                .height(50.dp)
                .clickable {
                    context.startActivity(Intent(context, Terminal::class.java))
                }){
                Text(text = stringResource(id = R.string.terminal), modifier = Modifier
                    .weight(0.8f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                    fontSize = 20.sp,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600)
            }
            Row (modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                .background(
                    colorResource(id = R.color.background2),
                    shape = RoundedCornerShape(14.dp)
                )
                .height(50.dp)
                .clickable { context.startActivity(Intent(context, Appeal::class.java)) }){
                Text(text = stringResource(id = R.string.appeal), modifier = Modifier
                    .weight(0.8f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                    fontSize = 20.sp,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600)
            }
            Row (modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                .background(
                    colorResource(id = R.color.background2),
                    shape = RoundedCornerShape(14.dp)
                )
                .height(50.dp)
                .clickable { context.startActivity(Intent(context, ChangePassword::class.java)) }){
                Text(text = stringResource(id = R.string.change_password), modifier = Modifier
                    .weight(0.8f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                    fontSize = 20.sp,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600)
            }
            Row (modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                .background(
                    colorResource(id = R.color.background2),
                    shape = RoundedCornerShape(14.dp)
                )
                .height(50.dp)) {
                Row(modifier = Modifier
                    .fillMaxWidth(1f)) {
                    Text(text = stringResource(id = R.string.dont_disturb),
                        fontSize = 20.sp,
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        modifier = Modifier
                            .weight(0.8f)
                            .align(Alignment.CenterVertically)
                            .padding(start = 10.dp))
                    Switch(checked = privacy, onCheckedChange
                    = {
                        privacy = it
                        coroutine.launch {
                            mDatabase.setPrivacy(privacy)
                            UpdatePrivacy().updatePrivacy(privacy, mDatabase.getToken()!!)
                        }
                      },
                        colors = SwitchDefaults
                            .colors(checkedThumbColor = colorResource(id = R.color.subscribe)),
                        modifier = Modifier.padding(end = 10.dp))
                }
            }
            Row(modifier = Modifier
                .align(Alignment.End)
                .padding(top = 10.dp, end = 30.dp)) {
                Button(onClick = {
                    coroutine.launch {
                        mDatabase.setLogin("")
                        mDatabase.collapseDatabase()
                    }

                    context.startActivity(Intent(context, LoginActivity::class.java)) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.background2))) {
                    Text(text = stringResource(id = R.string.exit),
                        fontSize = 20.sp, style=MaterialTheme.typography.bodyLarge,
                        color=colorResource(id=R.color.cred),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600)
                }
            }
        }
    }
}

fun createFileFromContentUri(fileUri : Uri, context: Context) : File {
    var fileName = ""

    fileUri.let { returnUri ->
        context.contentResolver.query(returnUri,null,null,null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
    }

    val fileType: String? = fileUri.let { returnUri ->
        context.contentResolver.getType(returnUri)
    }

    val iStream : InputStream =
        context.contentResolver.openInputStream(fileUri)!!
    val outputDir : File = context.cacheDir!!
    val outputFile : File = File(outputDir,fileName)
    copyStreamToFile(iStream, outputFile)
    iStream.close()
    return outputFile
}

fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
    inputStream.use { input ->
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
}