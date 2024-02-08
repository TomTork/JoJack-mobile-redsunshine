package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Alignment
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.elements.ChatActivity
import ru.anotherworld.jojack.elements.ChatMessage
import ru.anotherworld.jojack.elements.PostBase2
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class MainApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Content()
            }
        }
    }
}

val mDatabase = MainDatabase()
val getInfo = GetInfo()
val getPostVk = GetPostVk()
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "UnrememberedMutableState"
)
@Composable
private fun Content(){
    val context = LocalContext.current
    var start by remember { mutableStateOf(false) }
    try{
        if(mDatabase.getLogin() == "")context.startActivity(Intent(context, LoginActivity::class.java))
        else start = true
    } catch (io: Exception){
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
    if(start){
        val coroutine = rememberCoroutineScope()
        val interFamily = FontFamily(
            Font(R.font.inter600, FontWeight.W600),
        )
        val nunitoFamily = FontFamily(
            Font(R.font.nunito_semibold600, FontWeight.W600)
        )
        var contentManager by mutableStateOf(0)
        var select by remember { mutableStateOf(0) } //0 - Home; 1 - chat; 2 - settings
        Scaffold(
            modifier = Modifier.background(Color.White),
            topBar = {
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
                            Spacer(modifier = Modifier.padding(bottom=20.dp))
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Absolute.Right,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .fillMaxWidth(1f)
                                    .padding(end = 20.dp)) {
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
                                IconButton(onClick = {

                                }) {
                                    Image(painterResource(id = R.drawable.search2),
                                        null, modifier = Modifier.size(25.dp))
                                }

                            }
                        }

                    }

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
            when(contentManager){
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

@SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState",
    "UnrememberedMutableState"
)
@Composable
private fun NewsPaper(){
    val context = LocalContext.current
    var isServerConnect by remember { mutableStateOf(true) }
    val coroutine = rememberCoroutineScope()
    var maxId by remember { mutableIntStateOf(1) }
    var view by remember { mutableStateOf(false) }
    var view2 by remember { mutableStateOf(false) }
    val array = remember { listOf<SNews>().toMutableStateList() }
    val listState = rememberLazyListState()
    try {
        if(isServerConnect){
            Thread(Runnable {
                coroutine.launch {
                    if(!view2){
                        maxId = getInfo.getMaxId()
                        view2 = true
                    }
                    if(view2){
                        if(!view){
                            maxId -= 10
                            for(i in getPostVk.getPostVk(maxId, maxId + 9).post){
                                array.add(SNews(i.textPost, i.groupName, i.iconUrl, i.imagesUrls,
                                    i.originalUrl, i.like, i.exclusive))
                            }
                            view = true
                        }
                    }
                }
            }).start()
        }
    } catch (e: Exception){
        isServerConnect = false
        Log.e("Status connect", "BAD, FALSE ::MainApp")
    }

    Column (modifier = Modifier
        .padding(top = 50.dp, bottom = 60.dp)
        .fillMaxWidth(1f)
        .background(color = colorResource(id = R.color.background2)),
        verticalArrangement = Arrangement.Center) {

        if(view){
            LazyColumn(state = listState){
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
}

@Composable
private fun Messenger(){
//    val scrollState = rememberScrollState()
    val context = LocalContext.current
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
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun Account(){
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    var checked = remember { mutableStateOf(true) }
    Column(modifier = Modifier
        .padding(top = 60.dp, bottom = 60.dp)
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(colorResource(id = R.color.black2))) {
        val job = when(mDatabase.getLevel()){
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
            Icon(painterResource(id = R.drawable.account_circle), null,
                modifier = Modifier.size(150.dp))
            Column(modifier= Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = stringResource(id = R.string.login) + ": " + mDatabase.getLogin(),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W500)
                Text(text = stringResource(id = R.string.job) + " " + job,
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W500)
                Text(text = stringResource(id = R.string.ID) + " " + mDatabase.getServerId(),
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
                    Thread(Runnable {
                        mDatabase.setLogin("")
                        mDatabase.collapseDatabase()
                    }).start()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Search(){
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    var inputSearch by remember { mutableStateOf("") }
    val array = remember { listOf<SearchContent>().toMutableStateList() }
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)) {
        TopAppBar(title = {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .background(colorResource(id = R.color.background2))
                .align(Alignment.CenterHorizontally)) {
                IconButton(onClick = { context.startActivity(Intent(context, MainApp::class.java)) },
                    modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(painterResource(id = R.drawable.arrow_back), null,
                        tint = colorResource(id = R.color.white))
                }
                Text(text = stringResource(id = R.string.search),
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.align(Alignment.CenterVertically))
            }
        }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
            colors = TopAppBarDefaults
                .topAppBarColors(containerColor = colorResource(id = R.color.background2),
                    titleContentColor = colorResource(id = R.color.background2)))
        TextField(value = inputSearch, onValueChange = { inputSearch = it },
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            placeholder = { Text(text = stringResource(id = R.string.start_search),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp)
                    .alpha(0.5f),
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.white)) })
        //START SEARCHING
        LazyColumn(modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .padding(start = 10.dp, end = 10.dp)
            .background(
                color = colorResource(id = R.color.background2),
                shape = RoundedCornerShape(10.dp)
            )){
            items(array){ arr ->
                Column(modifier = Modifier.fillMaxWidth(1f)) {
                    Text(text = arr.head, fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        color = colorResource(id = R.color.white))
                    Text(text = arr.text, fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                        color = colorResource(id = R.color.white))
                }
            }
        }
    }

}

private data class SearchContent(
    val head: String,
    val text: String,
    val unit: () -> Unit
)
