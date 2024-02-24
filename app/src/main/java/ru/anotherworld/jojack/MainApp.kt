package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.animation.CrossSlide
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.elements.ChatActivity
import ru.anotherworld.jojack.elements.ChatMessage
import ru.anotherworld.jojack.elements.PostBase2
import ru.anotherworld.jojack.ui.theme.JoJackTheme
import java.io.File
import java.net.NoRouteToHostException
import io.ktor.client.network.sockets.ConnectTimeoutException
import ru.anotherworld.jojack.database.ChatsData


class MainApp : ComponentActivity() {
    @SuppressLint("PermissionLaunchedDuringComposition", "CoroutineCreationDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                val coroutine = rememberCoroutineScope()
                val context = LocalContext.current
                var start by remember { mutableStateOf(false) }
                try{ //Check first enter or no
                    coroutine.launch {
                        if(mDatabase.getLogin() == null || mDatabase.getLogin()!! == ""){
                            start = false
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                        else start = true
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
                        .weight(0.1f)
                        .fillMaxWidth(0.9f)) {
                    Text(text = stringResource(id = R.string.yet), fontFamily = nunitoFamily,
                        fontWeight = FontWeight.W500, modifier = Modifier,
                        color = colorResource(id = R.color.white))
                }
                Spacer(modifier = Modifier.weight(0.4f))
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
@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "UnrememberedMutableState"
)
@Composable
private fun Content(){ //Main Activity
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val interFamily = FontFamily(
        Font(R.font.inter600, FontWeight.W600),
    )
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600)
    )
    var contentManager by mutableIntStateOf(0)
    var select by remember { mutableIntStateOf(0) } //0 - Home; 1 - chat; 2 - settings
    var showSearchUser by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var searchUpdate by remember { mutableStateOf(false) }
    var searchExample: SearchP? = null
    val searchF = SearchIdOrName()
    Scaffold(
        modifier = Modifier.background(Color.White),
        topBar = {
            if (!showSearchUser){
                var topText by mutableStateOf(stringResource(id = R.string.home))
                topText = when(contentManager){
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
//                                    if (contentManager == 1){
//                                        IconButton(onClick = {
//                                            showSearchUser = !showSearchUser
//                                        }) {
//                                            Image(painterResource(id = R.drawable.add_circle),
//                                                null,
//                                                modifier = Modifier
//                                                    .align(Alignment.CenterVertically)
//                                                    .size(35.dp))
//                                        }
//                                    }
                                if(contentManager != 1) {
                                    IconButton(onClick = { /*TODO*/ }) {
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
                                else if(contentManager == 1){
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
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .background(colorResource(id = R.color.black2))
                    .padding(0.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { contentManager = 0 }
                        .weight(0.33f)) {
                    IconButton(onClick = {
                        contentManager = 0
                    },
                        modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Column(modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .offset(y = 3.dp)) {
                            Icon(painterResource(id = R.drawable.home21), null,
                                tint = if(contentManager == 0) colorResource(id = R.color.white) else colorResource(id = R.color.text_color),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Icon(painterResource(id = R.drawable.home22), null,
                                tint = if(contentManager == 0) colorResource(id = R.color.white) else colorResource(id = R.color.text_color),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .offset(y = (-6).dp)
                                    .scale(scaleX = 1.5f, scaleY = 1f))
                        }
                    }
                    Text(text = stringResource(id = R.string.home),
                        Modifier
                            .clickable { contentManager = 0 }
                            .shadow(2.dp)
                            .align(Alignment.CenterHorizontally),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        color = if(contentManager == 0) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                }
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { contentManager = 1 }
                        .weight(0.33f)) {
                    IconButton(onClick = { contentManager = 1 }) {
                        Icon(painterResource(id = R.drawable.comments2), null,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            tint = if(contentManager == 1) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                    }
                    Text(text = stringResource(id = R.string.message),
                        Modifier
                            .clickable { contentManager = 1 }
                            .shadow(2.dp)
                            .align(Alignment.CenterHorizontally),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        color = if(contentManager == 1) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                }
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { contentManager = 2 }
                        .weight(0.33f)) {
                    IconButton(onClick = { contentManager = 2 }) {
                        Icon(painterResource(id = R.drawable.settings2), null,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            tint = if(contentManager == 2) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                    }
                    Text(text = stringResource(id = R.string.settings),
                        Modifier
                            .clickable { contentManager = 2 }
                            .shadow(2.dp)
                            .align(Alignment.CenterHorizontally),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        color = if(contentManager == 2) colorResource(id = R.color.white) else colorResource(id = R.color.text_color))
                }
            }
        }
    ) {
        if (showSearchUser){ //Отобразить поиск

            var checkedSwitch by remember { mutableStateOf(false) }
            var checkedSwitch2 by remember { mutableStateOf(false) }
            val arrayPeopleIds = remember { listOf<String>().toMutableStateList() }

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
                Divider(thickness = 2.dp, color = colorResource(id = R.color.text_color),
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
                Divider(thickness = 2.dp, color = colorResource(id = R.color.text_color),
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

                                            } else { //chat without encryption

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
                                Divider(modifier = Modifier
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
            CrossSlide(targetState = contentManager, reverseAnimation = false){ screen ->
                when(screen){
                    0 -> { NewsPaper() }
                    1 -> { Messenger() }
                    2 -> { Account() }
                }
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
    val interFamily = FontFamily(
        Font(R.font.inter600, FontWeight.W600),
    )
    val context = LocalContext.current
    var isServerConnect by remember { mutableStateOf(true) }
    val coroutine = rememberCoroutineScope()
    var maxId by remember { mutableIntStateOf(1) }
    var view by remember { mutableStateOf(false) }
    var view2 by remember { mutableStateOf(false) }
    val array = remember { listOf<SNews>().toMutableStateList() }
    val listState = rememberLazyListState()
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_light400, FontWeight.W400),
        Font(R.font.nunito_semibold600, FontWeight.W600)
    )
    var job by remember { mutableIntStateOf(0) }
    var startCreateNewPostFromUser by remember { mutableStateOf(false) }
    if(isServerConnect){
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
            .padding(top = 50.dp, bottom = 60.dp)
            .fillMaxWidth(1f)
            .background(color = colorResource(id = R.color.background2)),
            verticalArrangement = Arrangement.Center) {

            if(view){
                LazyColumn(state = listState){
                    if(job >= 2){ //Функция создания постов доступна не для всех пользователей
                        item { //View listener to create new post from user
                            Row(modifier = Modifier
                                .fillMaxWidth(1f)
                                .clickable { startCreateNewPostFromUser = true }) {
                                Icon(painter = painterResource(id = R.drawable.account_circle),
                                    null, tint = colorResource(id = R.color.white),
                                    modifier = Modifier.size(30.dp))
                                Text(text = stringResource(id = R.string.write_something),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    fontFamily = nunitoFamily, fontWeight = FontWeight.W400,
                                    fontSize = 14.sp)
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
            else{ //show activity-no-internet
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
            .background(color = colorResource(id = R.color.background2))) {
            IconButton(onClick = { startCreateNewPostFromUser = false },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.Start)) {
                Icon(painter = painterResource(id = R.drawable.clear), contentDescription = null,
                    tint = colorResource(id = R.color.white))
            }
            Button(onClick = { /*Опубликовать пост*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent),
                modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(id = R.string.publish),
                    color = colorResource(id = R.color.white),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600)
            }
        }
        Divider(thickness = 2.dp, color = colorResource(id = R.color.white),
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
                .fillMaxHeight(1f),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = Color.White,
                disabledLabelColor = colorResource(id = R.color.ghost_white),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ))
    }
    
}

@Composable
private fun Messenger(){
//    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val array = remember { listOf<ChatsData>().toMutableStateList() }
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
                    action = { context.startActivity(Intent(context, ChatActivity::class.java)) })
            }
            itemsIndexed(array){ index, item ->
                ChatMessage(name = item.name, previewMessage = "", username = "",
                    action = {  }) //show chats
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
    val bitmap =  remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(colorResource(id = R.color.black2))
        .padding(top = 70.dp, bottom = 60.dp)) {

        coroutine.launch {
            if(mDatabase.getJob() == -1){
                coroutine.launch {
                    val initUser = InitUser()
                    val data = initUser.getInit(mDatabase.getLogin()!!, mDatabase.getToken()!!)
                    mDatabase.setServerId(data.id)
                    mDatabase.setJob(data.job)
                    mDatabase.setTrustLevel(data.trustLevel)
                }
            }
        }

        var ready by remember { mutableStateOf(false) }
        val launcher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }
        var job_ by remember { mutableIntStateOf(0) }
        var login_ by remember { mutableStateOf("") }
        var serverId_ by remember { mutableIntStateOf(0) }
        coroutine.launch {
            job_ = mDatabase.getJob()!!
            login_ = mDatabase.getLogin()!!
            serverId_ = mDatabase.getServerId()!!
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
                else {
                    Image(bitmap = bitmap.value!!.asImageBitmap(), null,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop)
                }
            }
            imageUri?.let {
                if(Build.VERSION.SDK_INT < 28){
                    bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    ready = true
                }
                else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                    ready = true
                }
            }
            Column(modifier= Modifier.align(Alignment.CenterHorizontally)) {
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

//DEPRECATED
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun Search(){
//    val nunitoFamily = FontFamily(
//        Font(R.font.nunito_semibold600, FontWeight.W600),
//        Font(R.font.nunito_medium500, FontWeight.W500)
//    )
//    var inputSearch by remember { mutableStateOf("") }
//    val array = remember { listOf<SearchContent>().toMutableStateList() }
//    val context = LocalContext.current
//    Column(modifier = Modifier
//        .fillMaxWidth(1f)
//        .fillMaxHeight(1f)) {
//        TopAppBar(title = {
//            Row(modifier = Modifier
//                .fillMaxWidth(1f)
//                .background(colorResource(id = R.color.background2))
//                .align(Alignment.CenterHorizontally)) {
//                IconButton(onClick = { context.startActivity(Intent(context, MainApp::class.java)) },
//                    modifier = Modifier.align(Alignment.CenterVertically)) {
//                    Icon(painterResource(id = R.drawable.arrow_back), null,
//                        tint = colorResource(id = R.color.white))
//                }
//                Text(text = stringResource(id = R.string.search),
//                    color = colorResource(id = R.color.white),
//                    modifier = Modifier.align(Alignment.CenterVertically))
//            }
//        }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
//            colors = TopAppBarDefaults
//                .topAppBarColors(containerColor = colorResource(id = R.color.background2),
//                    titleContentColor = colorResource(id = R.color.background2)))
//        TextField(value = inputSearch, onValueChange = { inputSearch = it },
//            modifier = Modifier
//                .fillMaxWidth(1f)
//                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
//            placeholder = { Text(text = stringResource(id = R.string.start_search),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 3.dp)
//                    .alpha(0.5f),
//                textAlign = TextAlign.Start,
//                color = colorResource(id = R.color.white)) })
//        //START SEARCHING
//        LazyColumn(modifier = Modifier
//            .fillMaxWidth(1f)
//            .fillMaxHeight(1f)
//            .padding(start = 10.dp, end = 10.dp)
//            .background(
//                color = colorResource(id = R.color.background2),
//                shape = RoundedCornerShape(10.dp)
//            )){
//            items(array){ arr ->
//                Column(modifier = Modifier.fillMaxWidth(1f)) {
//                    Text(text = arr.head, fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
//                        color = colorResource(id = R.color.white))
//                    Text(text = arr.text, fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
//                        color = colorResource(id = R.color.white))
//                }
//            }
//        }
//    }
//}

private data class SearchContent(
    val head: String,
    val text: String,
    val unit: () -> Unit
)
