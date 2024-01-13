package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.database.MainDatabase
import ru.anotherworld.jojack.ui.theme.JoJackTheme

val mDatabase = MainDatabase()
class MainApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            if(!mDatabase.id.exists() || mDatabase.getId() == -1)startActivity(Intent(this, MainActivity::class.java))
        } catch (IO: Exception){
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
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                )
                .background(colorResource(id = R.color.cred)),
            topBar = {
                var topText by mutableStateOf(stringResource(id = R.string.home))
                topText = when(contentManager){
                    0 -> stringResource(id = R.string.news)
                    1 -> stringResource(id = R.string.message)
                    2 -> stringResource(id = R.string.account)
                    else -> stringResource(id = R.string.home)
                }
                Surface(modifier = Modifier
                    .background(colorResource(id = R.color.cred))
                    .width(Dp.Infinity)) {
                    Row(modifier = Modifier
                        .width(Dp.Infinity)) {
                        Text(text = topText, fontSize = 35.sp,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .weight(2f),
                            fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = { /*TODO*/ },
                            modifier = Modifier
                                .weight(0.3f)
                                .size(50.dp)) {
                            Icon(imageVector = Icons.Filled.Search, null)
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
                            Modifier.clickable { contentManager = 0 }.shadow(2.dp),
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
        .padding(top = 30.dp, bottom = 60.dp)
        .clip(
            RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        ), verticalArrangement = Arrangement.Center) {
        for(i in 1..50){
            Text("$i", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp))
        }
    }
}

@Composable
private fun Messenger(){
    val scrollState = rememberScrollState()
    Column (modifier = Modifier
        .verticalScroll(scrollState)
        .padding(top = 30.dp, bottom = 60.dp)
        .clip(
            RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        ), verticalArrangement = Arrangement.Center) {
        for(i in 1..50){
            Text("Chat $i", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp))
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun Account(){
    val context = LocalContext.current
    var checked = remember { mutableStateOf(true) }
    Column(modifier = Modifier.padding(top = 80.dp, bottom = 60.dp)) {
        Row(modifier = Modifier.padding(start = 30.dp)) {
            Icon(painterResource(id = R.drawable.account_circle), null,
                modifier = Modifier.size(70.dp))
            Column {
                Text(text = stringResource(id = R.string.login))
                Text(text = stringResource(id = R.string.job) + " " + stringResource(id = R.string.jojack))
                Text(text = stringResource(id = R.string.ID))
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Row(modifier = Modifier
            .padding(30.dp)
            .background(
                colorResource(id = R.color.background_element),
                shape = RoundedCornerShape(14.dp)
            )
            .height(50.dp)) {
            Text(text = stringResource(id = R.string.theme), modifier = Modifier
                .weight(0.8f)
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp),
                fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Switch(checked = checked.value, onCheckedChange = { checked.value = it;
                mDatabase.setTheme(mDatabase.boolToInt(it)) },
                modifier = Modifier.weight(0.2f))
        }
        Row (modifier = Modifier
            .padding(start = 30.dp, end = 30.dp)
            .background(
                colorResource(id = R.color.background_element),
                shape = RoundedCornerShape(14.dp)
            )
            .height(50.dp).clickable { context.startActivity(Intent(context, Terminal::class.java)) }){
            Text(text = stringResource(id = R.string.terminal), modifier = Modifier
                .weight(0.8f)
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp),
                fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Row (modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, top = 30.dp)
            .background(
                colorResource(id = R.color.background_element),
                shape = RoundedCornerShape(14.dp)
            )
            .height(50.dp).clickable { context.startActivity(Intent(context, Appeal::class.java)) }){
            Text(text = stringResource(id = R.string.appeal), modifier = Modifier
                .weight(0.8f)
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp),
                fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Row (modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, top = 30.dp)
            .background(
                colorResource(id = R.color.background_element),
                shape = RoundedCornerShape(14.dp)
            )
            .height(50.dp).clickable { context.startActivity(Intent(context, ChangePassword::class.java)) }){
            Text(text = stringResource(id = R.string.change_password), modifier = Modifier
                .weight(0.8f)
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp),
                fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Row(modifier = Modifier
            .align(Alignment.End)
            .padding(top = 30.dp, end = 30.dp)) {
            Button(onClick = { mDatabase.collapseDatabase(); context.startActivity(Intent(context, MainActivity::class.java)) }) {
                Text(text = stringResource(id = R.string.exit), fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }


    }


}