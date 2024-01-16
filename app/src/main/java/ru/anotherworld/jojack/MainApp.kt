package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.elements.ChatMessage
import ru.anotherworld.jojack.elements.PostBase
import ru.anotherworld.jojack.ui.theme.JoJackTheme

val mDatabase = MainDatabase()
class MainApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            if(!mDatabase.id.exists() || mDatabase.getId() == -1)startActivity(Intent(this, MainActivity::class.java))
        } catch (io: Exception){
            startActivity(Intent(this, MainActivity::class.java))
        }
        setContent {
            JoJackTheme {
                Content()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "UnrememberedMutableState"
)
@Composable
fun Content(){
    val coroutine = rememberCoroutineScope()
    var contentManager by mutableStateOf(0)
    var visible by remember {
        mutableStateOf(true)
    }
    AnimatedVisibility(visible = visible,
        enter = slideInHorizontally(tween(durationMillis = 3000,
            easing = LinearEasing)) + expandHorizontally(expandFrom = Alignment.End) + fadeIn(tween(durationMillis = 3000,
            easing = LinearEasing)),
        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                + shrinkHorizontally() + fadeOut(tween(durationMillis = 3000)),
    ) {
        Scaffold(
            modifier = Modifier.background(Color.White),
            topBar = {
                var topText by mutableStateOf(stringResource(id = R.string.home))
                topText = when(contentManager){
                    0 -> stringResource(id = R.string.news)
                    1 -> stringResource(id = R.string.message)
                    2 -> stringResource(id = R.string.account)
                    else -> stringResource(id = R.string.home)
                }
                Surface(modifier = Modifier
                    .background(Color.White)
                    .width(Dp.Infinity)) {
                    Row(modifier = Modifier
                        .width(Dp.Infinity)
                        .padding(start = 10.dp)) {
                        Text(text = topText, fontSize = 35.sp,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .weight(2f),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold)
                        IconButton(onClick = { /*TODO*/ },
                            modifier = Modifier
                                .weight(0.3f)
                                .size(50.dp)) {
                            Icon(imageVector = Icons.Filled.Search, null,
                                modifier = Modifier.padding(end=10.dp))
                        }
                    }
                }
            },
            bottomBar = {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .background(colorResource(id = R.color.black))
                        .padding(0.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { contentManager = 0 }) {
                        IconButton(onClick = { contentManager = 0 }) {
                            Icon(painterResource(id = R.drawable.newspaper), null)
                        }
                        Text(text = stringResource(id = R.string.news),
                            Modifier
                                .clickable { contentManager = 0 }
                                .shadow(2.dp),
                            fontWeight = boldNews(contentManager))
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { contentManager = 1 }) {
                        IconButton(onClick = { contentManager = 1 }) {
                            Icon(painterResource(id = R.drawable.message), null)
                        }
                        Text(text = stringResource(id = R.string.message),
                            Modifier.clickable { contentManager = 1 },
                            fontWeight = boldMessenger(contentManager)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { contentManager = 2 }) {
                        IconButton(onClick = { contentManager = 2 }) {
                            Icon(painterResource(id = R.drawable.account_circle), null)
                        }
                        Text(text = stringResource(id = R.string.account),
                            Modifier.clickable { contentManager = 2 },
                            fontWeight = boldAccount(contentManager)
                        )
                    }
                    
                }
            }
        ) {
            when(contentManager){
                0 -> NewsPaper()
                1 -> Messenger()
                2 -> Account()
            }
        }
    }

}

private fun boldNews(contentManager: Int): FontWeight{
    if(contentManager == 0)return FontWeight.ExtraBold
    return FontWeight.Normal
}
private fun boldMessenger(contentManager: Int): FontWeight{
    if(contentManager == 1)return FontWeight.ExtraBold
    return FontWeight.Normal
}
private fun boldAccount(contentManager: Int): FontWeight{
    if(contentManager == 2)return FontWeight.ExtraBold
    return FontWeight.Normal
}

@Composable
fun ShowAnimatedText(
    text : String?,
    show: Boolean
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(animationSpec = tween(2000)),
            exit = fadeOut(animationSpec = tween(2000))
        ) {

            text?.let {
                Text(text = it)
            }
        }
    }
}

@Composable
private fun NewsPaper(){
    val scrollState = rememberScrollState()
    Column (modifier = Modifier
        .verticalScroll(scrollState)
        .padding(top = 50.dp, bottom = 60.dp)
        .clip(
            RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        ), verticalArrangement = Arrangement.Center) {
        val text = "Это длинный и осмысленный текст, таких текстов много, но этот длинный и осмысленный текст - один!"
        for(i in 1..20){
            PostBase(
                idPost = i, text = text, existsImage = true,
                image = ImageBitmap.imageResource(R.drawable.error))
        }
    }
}

@Composable
private fun Messenger(){
//    val scrollState = rememberScrollState()
    Column (modifier = Modifier
        .padding(top = 50.dp, bottom = 60.dp)
        .clip(
            RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        ), verticalArrangement = Arrangement.Center) {
        val listSize = 20
        LazyColumn{
            items(20){
                ChatMessage(name = "CHAT", previewMessage = "Как дела?", username = "Вы")
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun Account(){
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var checked = remember { mutableStateOf(true) }
    Column(modifier = Modifier.padding(top = 50.dp, bottom = 60.dp)) {
        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Icon(painterResource(id = R.drawable.account_circle), null,
                modifier = Modifier.size(150.dp))
            Column(modifier= Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = stringResource(id = R.string.login) + ": ",
                    style=MaterialTheme.typography.labelLarge)
                Text(text = stringResource(id = R.string.job) + " " + stringResource(id = R.string.jojack),
                    style=MaterialTheme.typography.labelLarge)
                Text(text = stringResource(id = R.string.ID),
                    style=MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Column(Modifier.verticalScroll(scrollState)) {
            Row(modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .background(
                    colorResource(id = R.color.background_element),
                    shape = RoundedCornerShape(14.dp)
                )
                .height(50.dp)) {
                Text(text = stringResource(id = R.string.theme), modifier = Modifier
                    .weight(0.8f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                    fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    style=MaterialTheme.typography.bodyLarge)
                Switch(checked = checked.value, onCheckedChange = { checked.value = it;
                    mDatabase.setTheme(mDatabase.boolToInt(it)); changeTheme(it) },
                    modifier = Modifier.weight(0.2f), colors=SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.cred)
                    ))
            }
            Row (modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                .background(
                    colorResource(id = R.color.background_element),
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
                    fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    style=MaterialTheme.typography.bodyLarge)
            }
            Row (modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                .background(
                    colorResource(id = R.color.background_element),
                    shape = RoundedCornerShape(14.dp)
                )
                .height(50.dp)
                .clickable { context.startActivity(Intent(context, Appeal::class.java)) }){
                Text(text = stringResource(id = R.string.appeal), modifier = Modifier
                    .weight(0.8f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                    fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    style=MaterialTheme.typography.bodyLarge)
            }
            Row (modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                .background(
                    colorResource(id = R.color.background_element),
                    shape = RoundedCornerShape(14.dp)
                )
                .height(50.dp)
                .clickable { context.startActivity(Intent(context, ChangePassword::class.java)) }){
                Text(text = stringResource(id = R.string.change_password), modifier = Modifier
                    .weight(0.8f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                    fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    style=MaterialTheme.typography.bodyLarge)
            }
            Row(modifier = Modifier
                .align(Alignment.End)
                .padding(top = 10.dp, end = 30.dp)) {
                Button(onClick = { mDatabase.collapseDatabase(); context.startActivity(Intent(context, MainActivity::class.java)) }) {
                    Text(text = stringResource(id = R.string.exit), fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, style=MaterialTheme.typography.bodyLarge,
                        color=colorResource(id=R.color.cred))
                }
            }
        }
    }
}

private fun changeTheme(value: Boolean){
    if(value){

    }
}



//private val SaveMap = mutableMapOf<String, MutableList<KeyParams>>()
//
//private val lastScreenName: String?
//    get() = ""
//
//private class KeyParams(
//    // Это ключ для вложенного списка.
//    // Если на экране будет только один скроллящийся элемент
//    // это поле будет пустым
//    val params: String,
//    val index: Int,
//    val scrollOffset: Int,
//)
//@Composable
//private fun rememberForeverLazyListState(
//    params: String = "",
//): LazyListState {
//    val key = lastScreenName ?: return rememberLazyListState()
//    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
//        val savedValue = getSavedValue(key, params)
//        LazyListState(
//            savedValue?.index.orDefault(),
//            savedValue?.scrollOffset.orDefault()
//        )
//    }
//    DisposableEffect(params) {
//        onDispose {
//            val lastIndex = scrollState.firstVisibleItemIndex
//            val lastOffset = scrollState.firstVisibleItemScrollOffset
//            addNewValue(key, KeyParams(params, lastIndex, lastOffset))
//        }
//    }
//    return scrollState
//}
//
////@Composable
////fun DynamicThemeToggler() {
////    var isDarkTheme by remember { mutableStateOf(false) }
////    Switch(checked=isDarkTheme, onCheckedChange = { isDarkTheme = it } )
////    MaterialTheme(colors=if (isDarkTheme) DarkColorPalette else LightColorPalette) {
////    // Your app's screen content goes here }}
////    }
////}