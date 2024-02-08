package ru.anotherworld.jojack.elements

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.MainApp
import ru.anotherworld.jojack.R
import ru.anotherworld.jojack.VkImageAndVideo
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class MessengerPostActivity
    : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Content(
                    constructorMessenger.idPost!!, constructorMessenger.text!!,
                    constructorMessenger.nameGroup!!, constructorMessenger.iconGroup!!,
                    constructorMessenger.typeGroup!!, true,
                    constructorMessenger.images!!, constructorMessenger.originalUrl!!,
                    constructorMessenger.like!!, constructorMessenger.exclusive!!,
                    constructorMessenger.inMessenger!!
                )
            }
        }
    }
}

data class ConstructorMessenger(
    var idPost: Int?, var text: String?, var nameGroup: String?,
    var iconGroup: String?, var typeGroup: String?, private val existsImages: Boolean? = false,
    var images: VkImageAndVideo?, var originalUrl: String?, var like: Int?,
    var exclusive: Boolean?, var inMessenger: Boolean? = true
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun Content(idPost: Int, text: String, nameGroup: String, iconGroup: String,
                    typeGroup: String, existsImages: Boolean = false, images: VkImageAndVideo,
                    originalUrl: String, like: Int, exclusive: Boolean, inMessenger: Boolean = false){
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    val scroll = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(colorResource(id = R.color.background2))) {
        Scaffold(topBar = {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .background(colorResource(id = R.color.background2))) {
                IconButton(onClick = {
                    context.startActivity(Intent(context, MainApp::class.java))
                }) {
                    Icon(painterResource(id = R.drawable.arrow_back), null,
                        tint = colorResource(id = R.color.white))
                }
                Text(text = stringResource(id = R.string.comments),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                    fontSize = 27.sp, modifier = Modifier.align(Alignment.CenterVertically))
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
                            Icon(painterResource(id = R.drawable.attach), null,
                                modifier = Modifier
                                    .padding(start = 7.dp)
                                    .size(30.dp))
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            coroutine.launch {
                                //SEND COMMENT
                            }
                        }) {
                            Icon(painterResource(id = R.drawable.send2), null,
                                modifier = Modifier
                                    .padding(end = 7.dp)
                                    .size(28.dp))
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .background(colorResource(id = R.color.background2))) {
            Column(modifier = Modifier
                .background(colorResource(id = R.color.black2))) {
//                PostBase2(idPost, text, nameGroup, iconGroup, typeGroup, existsImages, images,
//                    originalUrl, like, exclusive, inMessenger)
                Divider(thickness = 5.dp, color = Color.Black)
                LazyColumn{
                    //Ссылки на ветку комментариев
                }
            }

        }
    }

}