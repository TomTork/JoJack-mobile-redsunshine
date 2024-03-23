package ru.anotherworld.jojack.elements

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.InsertChat
import ru.anotherworld.jojack.R
import ru.anotherworld.jojack.chatsDatabase
import ru.anotherworld.jojack.database.ChatsData
import ru.anotherworld.jojack.database.ChatsDatabase
import ru.anotherworld.jojack.interFamily
import ru.anotherworld.jojack.mDatabase
import ru.anotherworld.jojack.nunitoFamily

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ChatMessage(name: String, previewMessage: String, username: String, idChat: Int = 0,
                image: ImageBitmap? = null, countMessage: Int = 0, url: String = "",
                action: (id: Int) -> Unit,
                listener: MutableState<Boolean> = mutableStateOf(false)) {
    var expanded by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    Column(modifier = Modifier
        .padding(top = 4.dp)
        .fillMaxWidth(1f)
        .background(colorResource(id = R.color.black2))
        .clickable { action(idChat) }
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    expanded = true
                },
                onTap = {
                    action(idChat)
                }
            )
        }) {
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
            offset = DpOffset(x = 10.dp, y = 0.dp),
            modifier = Modifier
                .background(
                    color = colorResource(id = R.color.background2),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(start = 5.dp, end = 5.dp)) {
            Column(modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)) {
                Row(modifier = Modifier
                    .clickable {
                        coroutine.launch {
                            chatsDatabase.deleteByName(name)
                            val insertChat = InsertChat()
                            insertChat.deleteChatInfo(url)
                            listener.value = true
                            expanded = false
                        }
                    }){
                    Text(text = stringResource(id = R.string.delete_chat),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterVertically))
                    Icon(painter = painterResource(id = R.drawable.delete_forever),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically))

                }
//                HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth(1f),
//                    color = colorResource(id = R.color.icon_color))

            }
        }
        HorizontalDivider(color = colorResource(id = R.color.background2),
            thickness = 1.dp, modifier = Modifier.alpha(0.2f))
        Row {
            if(image == null){
                Icon(
                    painterResource(id = R.drawable.account_circle), null,
                    modifier = Modifier
                        .size(70.dp)
                        .padding(start = 10.dp),
                    tint = Color.White)
            }
            else{
                Icon(image, null, Modifier
                    .size(70.dp)
                    .padding(start = 10.dp))
            }
            Column(modifier = Modifier
                .align(Alignment.CenterVertically)) {
                Text(text = name, fontWeight = FontWeight.W600, fontSize = 18.sp,
                    fontFamily = nunitoFamily,
                    modifier = Modifier
                        .padding(start = 5.dp),
                    color = colorResource(id = R.color.white))
                if (countMessage != 0){
                    Text(text = countMessage.toString(), modifier = Modifier
                        .background(colorResource(id = R.color.grey),
                            shape = RoundedCornerShape(20.dp)),
                        color = Color.White
                    )
                }
                Text(text = "$username: $previewMessage", modifier = Modifier.padding(start = 5.dp,
                        bottom = 5.dp), fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                    fontSize = 16.sp, color = colorResource(id = R.color.white))
                Spacer(modifier = Modifier.padding(bottom=4.dp))
            }
        }
    }
}