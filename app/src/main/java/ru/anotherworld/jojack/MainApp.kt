package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.elements.ChatMessage
import ru.anotherworld.jojack.elements.PostBase2
import ru.anotherworld.jojack.ui.theme.JoJackTheme

val mDatabase = MainDatabase()
class MainApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            if(!mDatabase.id.exists() || mDatabase.getId() == -1)startActivity(Intent(this, LoginActivity::class.java))
        } catch (io: Exception){
            startActivity(Intent(this, LoginActivity::class.java))
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
private fun Content(){
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val interFamily = FontFamily(
        Font(R.font.inter600, FontWeight.W600),
    )
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600)
    )
    var isVisible by remember { mutableStateOf(true) }
    var check by remember { mutableStateOf(0) }
    var contentManager by mutableStateOf(0)
    var visible by remember {
        mutableStateOf(true)
    }
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

                            Image(painterResource(id = R.drawable.search2),
                                null, modifier = Modifier.size(25.dp))
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
                    IconButton(onClick = { contentManager = 0 },
                        modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Column(modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .offset(y = 3.dp)) {
                            Icon(painterResource(id = R.drawable.home21), null,
                                tint = colorResource(id = R.color.text_color),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Icon(painterResource(id = R.drawable.home22), null,
                                tint = colorResource(id = R.color.text_color),
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
                        color = colorResource(id = R.color.text_color))
                }
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { contentManager = 1 }
                        .weight(0.33f)) {
                    IconButton(onClick = { contentManager = 1 }) {
                        Icon(painterResource(id = R.drawable.comments2), null,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            tint = colorResource(id = R.color.text_color))
                    }
                    Text(text = stringResource(id = R.string.message),
                        Modifier
                            .clickable { contentManager = 1 }
                            .shadow(2.dp)
                            .align(Alignment.CenterHorizontally),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        color = colorResource(id = R.color.text_color))
                }
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { contentManager = 2 }
                        .weight(0.33f)) {
                    IconButton(onClick = { contentManager = 2 }) {
                        Icon(painterResource(id = R.drawable.settings2), null,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            tint = colorResource(id = R.color.text_color))
                    }
                    Text(text = stringResource(id = R.string.settings),
                        Modifier
                            .clickable { contentManager = 2 }
                            .shadow(2.dp)
                            .align(Alignment.CenterHorizontally),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        color = colorResource(id = R.color.text_color))
                }
            }
        }
    ) {
        when(contentManager){
            0 -> {
                isVisible = true
                when(check){
                    0 -> NewsPaper()
                    1 -> Meetings()
                }

            }
            1 -> {Messenger(); isVisible = false}
            2 -> {Account(); isVisible = false}
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
private fun Meetings(){

}

@Composable
private fun NewsPaper(){
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    Column (modifier = Modifier
        .verticalScroll(scrollState)
        .padding(top = 50.dp, bottom = 60.dp)
        .fillMaxWidth(1f)
        .background(color = colorResource(id = R.color.background2)),
        verticalArrangement = Arrangement.Center) {
        val text = "Это длинный и осмысленный текст, таких текстов много, но этот длинный и осмысленный текст - один!"
        for(i in 1..20){
            PostBase2(idPost=i, text=text, nameGroup="Стас Ай, Как Просто",
                iconGroup=painterResource(id = R.drawable.group),
                typeGroup="Паблик", existsImages = false,
                images = listOf(painterResource(id = R.drawable.preview))
            )
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
                Button(onClick = { mDatabase.collapseDatabase(); context.startActivity(Intent(context, LoginActivity::class.java)) }) {
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
