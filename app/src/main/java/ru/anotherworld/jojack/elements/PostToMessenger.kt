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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import ru.anotherworld.jojack.ChatTwo
import ru.anotherworld.jojack.Messenger
import ru.anotherworld.jojack.R
import ru.anotherworld.jojack.TMessage2
import ru.anotherworld.jojack.VkImageAndVideo
import ru.anotherworld.jojack.chatsDatabase
import ru.anotherworld.jojack.database.ChatsData
import ru.anotherworld.jojack.mDatabase
import ru.anotherworld.jojack.ui.theme.JoJackTheme

lateinit var copyPost: CopyPost

class PostToMessenger : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                Messenger(Json.encodeToString<CopyPost>(copyPost))
            }
        }
    }
}

@Serializable
data class CopyPost(
    val idPost: Int,
    val text: String,
    val nameGroup: String,
    val iconGroup: String,
    val typeGroup: String,
    val existsImage: Boolean,
    val images: VkImageAndVideo,
    val originalUrl: String,
    val like: Int,
    val exclusive: Boolean
)

//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//private fun Content(){ //Отображение перессылки сообщений
//    val array = remember { listOf<ChatsData>().toMutableStateList() }
//    val coroutine = rememberCoroutineScope()
//    var previewName by remember { mutableStateOf("") }
//    var previewMessage by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    var login by rememberSaveable { mutableStateOf("") }
//    coroutine.launch {
//        array.addAll(chatsDatabase.getAll())
//        login = mDatabase.getLogin()!!
//    }
//    Column(modifier = Modifier
//        .fillMaxWidth(1f)
//        .fillMaxHeight(1f)
//        .clip(
//            RoundedCornerShape(
//                topStart = 30.dp,
//                topEnd = 30.dp,
//                bottomEnd = 0.dp,
//                bottomStart = 0.dp
//            )
//        )
//        .background(colorResource(id = R.color.black2))) {
//        LazyColumn{
//            itemsIndexed(array){ index, item ->
//                coroutine.launch {
//                    val chatTwo = ChatTwo(item.chat)
//                    try{
//                        val count = chatTwo.getCountMessages()!!
//                        val value = chatTwo.getRangeMessages(count, count)
//                        if("[|START|]" in value[0].message){
//                            val dd = Json.decodeFromString<CopyPost>(value[0].message
//                                .substringAfter("[|START|]").substringBefore("[|END|]"))
//                            previewMessage = dd.nameGroup + " " + dd.text.substring(0, 16) + "..."
//                        } else previewMessage = value[0].message
//                        previewName = value[0].author
//                    } catch (e: Exception){
//                        Log.e("ERROR", e.message.toString())
//                    }
//                }
//                ChatMessage(name = item.name, previewMessage = previewMessage, username = previewName,
//                    action = {
//                        coroutine.launch {
//                            idChat2 = item.chat
//                            nameChat2 = item.name
//                            iconChat2 = item.icon
//                            repost = Json.encodeToString<CopyPost>(copyPost)
//                            context.startActivity(Intent(context, Chat2::class.java))
//                        }
//
//                    }) //show chats
//            }
//        }
//    }
//}