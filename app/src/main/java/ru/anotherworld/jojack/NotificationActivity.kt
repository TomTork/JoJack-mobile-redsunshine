@file:OptIn(ExperimentalMaterial3Api::class)

package ru.anotherworld.jojack

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ru.anotherworld.jojack.ui.theme.JoJackTheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.database.ChatsDatabase
import ru.anotherworld.jojack.database.NData
import ru.anotherworld.jojack.database.NotificationData
import ru.anotherworld.jojack.database.NotificationsDatabase
import ru.anotherworld.jojack.elements.Chat2
import ru.anotherworld.jojack.elements.encChat
import ru.anotherworld.jojack.elements.iconChat2
import ru.anotherworld.jojack.elements.idChat2
import ru.anotherworld.jojack.elements.nameChat2
import ru.anotherworld.jojack.elements.repost
import kotlin.math.roundToInt
import kotlin.random.Random

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {
                MissingPermissionsComponent {
                    Content()
                }
            }
        }
    }
}

val notificationsDatabase = NotificationsDatabase()

@SuppressLint("CoroutineCreationDuringComposition")
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(){
    val context = LocalContext.current
    val notificationsRead = mutableListOf<NData>()
    val notificationsUnread = mutableListOf<NData>()
    val coroutine = rememberCoroutineScope()
    coroutine.launch {
        val list = notificationsDatabase.getAll()
        list.forEach {
            if(it.read) notificationsRead.add(it)
            else notificationsUnread.add(it)
        }
    }
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(colorResource(id = R.color.black2))) {
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
                Text(text = stringResource(id = R.string.notification),
                    color = colorResource(id = R.color.white),
                    fontFamily = nunitoFamily,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.align(Alignment.CenterVertically))
            }
        }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
            colors = TopAppBarDefaults
                .topAppBarColors(containerColor = colorResource(id = R.color.background2),
                    titleContentColor = colorResource(id = R.color.background2)))

        LazyColumn(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)){
            item {
                if (notificationsUnread.size > 0){
                    Text(text = stringResource(id = R.string.unread),
                        fontFamily = nunitoFamily,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .alpha(0.5f)
                            .align(Alignment.Start)
                            .padding(start = 10.dp))
                }
            }
            itemsIndexed(notificationsUnread){ index, value ->
                Notificator(head = value.label, text = value.text, id = index,
                    action = value.action, notificationsRead, notificationsUnread,
                    state = true, globalId = value.id)
            }
            item {
                if (notificationsRead.size > 0){
                    Text(text = stringResource(id = R.string.read),
                        fontFamily = nunitoFamily,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .alpha(0.5f)
                            .align(Alignment.Start)
                            .padding(start = 10.dp))
                }
            }
            itemsIndexed(notificationsRead){ index, value ->
                Notificator(head = value.label, text = value.text, id = index,
                    action = value.action, notificationsRead, notificationsUnread,
                    state = false, globalId = value.id)
            }
        }
    }

}

@Composable
private fun Notificator(head: String, text: String, id: Int, action: String,
                        read: MutableList<NData>, unread: MutableList<NData>,
                        state: Boolean, globalId: Int){
    val inAction = interAction(action)
    val chatsDatabase = ChatsDatabase()
    val context = LocalContext.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val coroutine = rememberCoroutineScope()
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)
        .background(
            colorResource(id = R.color.background2),
            shape = RoundedCornerShape(20.dp)
        )
        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()

                val (x, y) = dragAmount
                when {
                    x > 0 -> {
                        coroutine.launch {
                            if (state) {
                                read.remove(NData(id, head, text, true, action))
                                unread.add(NData(id, head, text, false, action))
                                notificationsDatabase.updateRead(globalId, false)
                            } else {
                                unread.remove(NData(id, head, text, false, action))
                                read.add(NData(id, head, text, true, action))
                                notificationsDatabase.updateRead(globalId, true)
                            }
                        }
                    }
                }
                offsetX += dragAmount.x
                offsetY += dragAmount.y
            }
        }
        .clickable {
            when (inAction.act) {
                "chat" -> {
                    coroutine.launch {
                        val info = chatsDatabase.getInfoByUrl(inAction.url)
                        if (info != null) {
                            idChat2 = inAction.url
                            nameChat2 = info.name
                            iconChat2 = info.icon
                            repost = ""
                            encChat = "echat" in inAction.url || "emchat" in inAction.url
                            context.startActivity(Intent(context, Chat2::class.java))
                        }
                    }
                }

                "post" -> {
                    Toast
                        .makeText(context, "Content in develop!", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> Toast
                    .makeText(context, context.getText(R.string.unknown_action), Toast.LENGTH_LONG)
                    .show()
            }
        }) {
        Text(text = head, fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
            color = colorResource(id = R.color.white))
        Text(text = text, fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
            color = colorResource(id = R.color.white))
    }
}

data class Action(
    val act: String,
    val url: String,
    val label: String
)

fun interAction(action: String): Action{
    val mas = action.split(";")
    return Action(mas[0], mas[1], mas[2])
}

fun generateAction(act: String? = null, url: String? = null, label: String? = null): String{
    var action = if(act == null) ";" else "$act;"
    action += if(url == null) ";" else "$url;"
    action += if(label != null && label.length <= 128 - action.length) label else if(label == null) "" else label.substring(0, 125 - action.length) + "..."
    return action
}

class NotifyManager{
    private val notifyListener = NotifyListener()
    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val notificationsDatabase = NotificationsDatabase()
    fun listen(context: Context){
        Thread(Runnable {
            job.start()
            while (true){
                ioScope.launch {
                    val get = notifyListener.getNotify()
                    if(get.isNotEmpty()){
                        for(n in get){
                            notificationsDatabase.insertAll(
                                NotificationData(
                                    label = n.label.substringAfterLast(";"),
                                    text = n.text,
                                    read = n.read,
                                    action = generateAction(
                                        if(n.label.substringBefore(";") == "") null else n.label.substringBefore(";"),
                                        if(n.label.substringAfter(";").substringBeforeLast(";") == "") null else n.label.substringAfter(";").substringBeforeLast(";"),
                                        if(n.label.substringAfterLast(";") == "") null else n.label.substringAfterLast(";")
                                ))
                            )
                            NotificationService(context = context)
                                .showBasicNotification(
                                    n.label.substringAfterLast(";"),
                                    n.text)
                        }
                    }
                }
            }
        }).start()
    }
}

class NotificationService(
    val context: Context
){
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val intent = Intent(context, NotificationActivity::class.java)
    init {
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    private val pendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                or PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE)


    fun showBasicNotification(head: String, text: String){
        val notification=NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(head)
            .setContentText(text)
            .setSmallIcon(R.drawable.jojack_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showExpandableNotification(head: String, text: String){
        val notification=NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(head)
            .setContentText(text)
            .setSmallIcon(R.drawable.jojack_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat
                    .BigPictureStyle()
                    .bigPicture(
                        context.bitmapFromResource(
                            R.drawable.jojack_icon
                        )
                    )
            )
            .build()
        notificationManager.notify(Random.nextInt(),notification)
    }

    private fun Context.bitmapFromResource(
        @DrawableRes resId:Int
    ): Bitmap = BitmapFactory.decodeResource(
        resources,
        resId
    )
}