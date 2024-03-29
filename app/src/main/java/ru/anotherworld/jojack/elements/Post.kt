package ru.anotherworld.jojack.elements

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.LikeController
import ru.anotherworld.jojack.R
import ru.anotherworld.jojack.VkImageAndVideo
import ru.anotherworld.jojack.database.LikesDatabase
import ru.anotherworld.jojack.database.MainDatabase
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.lerp
import coil.compose.SubcomposeAsyncImage
import ru.anotherworld.jojack.chatcontroller.getCurrentTimeStamp
import ru.anotherworld.jojack.currentImageModel
import ru.anotherworld.jojack.database.Comments
import ru.anotherworld.jojack.interFamily
import ru.anotherworld.jojack.mDatabase
import ru.anotherworld.jojack.nunitoFamily
import ru.anotherworld.jojack.showBars
import ru.anotherworld.jojack.showFullScreenImage
import java.io.File
import kotlin.math.absoluteValue


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBase2(idPost: Int, text: String, nameGroup: String, iconGroup: String,
              typeGroup: String, existsImages: Boolean = false, images: VkImageAndVideo,
              originalUrl: String, like: Int, exclusive: Boolean, inMessenger: Boolean = false){
    val likesDatabase = LikesDatabase()
    val context = LocalContext.current
    val likeController = LikeController()
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState { images.images.size }
    var checked by remember { mutableStateOf(false) }
    var startComments by remember { mutableStateOf(false) }
    val past = File("data/data/ru.anotherworld.jojack/icon.png")

    coroutine.launch {
        val previewChecked = likesDatabase.getLikedByOriginalUrl(originalUrl)
        if (previewChecked != null){
            if(previewChecked == true){
                checked = true
            }
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var uLike by remember { mutableIntStateOf(like) }
    val sheetState = rememberModalBottomSheetState()

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    Column(modifier= Modifier
        .padding(bottom = 7.dp)
        .fillMaxWidth(1f)
        .background(
            color = colorResource(id = R.color.black2),
            shape = RoundedCornerShape(20.dp)
        )) {
        Spacer(modifier=Modifier.padding(top=21.dp))
        Row(Modifier.padding(start=10.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconGroup)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.preview),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Column(modifier=Modifier.padding(start=10.dp)) {
                Text(text=nameGroup, color=colorResource(id=R.color.white),
                    fontFamily=nunitoFamily, fontWeight=FontWeight.W600,
                    lineHeight = 20.sp)
                Text(text=typeGroup, color=colorResource(id=R.color.type_group),
                    fontFamily=nunitoFamily, fontWeight=FontWeight.W600,
                    modifier=Modifier.offset(y=(-4).dp))
            }
        }
        HashtagsMentionsTextView(text = text, onClick = {}, modifier = Modifier.padding(bottom=4.dp, start=10.dp, end=10.dp),
            fontFamily = nunitoFamily, fontWeight = FontWeight.W500)
        if(existsImages && images.images.isNotEmpty()){
            HorizontalPager(state = pagerState,
                key = { images.images[it] },
                pageSize = PageSize.Fill,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterHorizontally)) {index ->
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(images.images[index])
                    .crossfade(true)
                    .build()
                Card(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxHeight(0.1f)
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - index) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .clickable {
                        showFullScreenImage.value = true
                        currentImageModel.value = model
                    }
                ) {

                    Box(modifier = Modifier
                        .fillMaxWidth(1f)
                        .align(Alignment.CenterHorizontally)) {

                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(1f)
                                .size(400.dp)
                                .blur(80.dp)
                        )
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(400.dp)
                        )

                        Box(modifier = Modifier
                            .padding(top = 5.dp, end = 5.dp)
                            .align(Alignment.TopEnd)
                            .graphicsLayer {
                                val pageOffset = (
                                        (pagerState.currentPage - index) + pagerState
                                            .currentPageOffsetFraction
                                        ).absoluteValue
                                alpha = lerp(
                                    start = 0.4f,
                                    stop = 0.5f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                            }) {
                            if(index > 0){
                                Text(text = " ${index+1} / ${images.images.size} ",
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(
                                            color = colorResource(id = R.color.background_field),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    fontFamily = interFamily, fontWeight = FontWeight.W400,
                                    fontSize = 15.sp)
                            }

                        }

                    }
                }
            }
        }
        Row(modifier=Modifier.padding(top=4.dp, start=10.dp, end=21.dp)) {
            IconButton(onClick = {
                coroutine.launch {
                    likeController.newLike(originalUrl, !checked, MainDatabase().getToken()!!)
                    checked = !checked
                    if (checked) uLike += 1
                    else uLike -= 1

                    //Регистрация лайков пользователя в бд
                    if(checked && likesDatabase.getLikedByOriginalUrl(originalUrl) == null) likesDatabase.insertAll(originalUrl)
                    else if(!checked) likesDatabase.setLikedByOriginalUrl(originalUrl, false)
                    else if(checked) likesDatabase.setLikedByOriginalUrl(originalUrl, true)
                }
            }) {
                Icon(painterResource(id=R.drawable.like), "Like",
                    tint = if(checked) colorResource(id = R.color.cred) else colorResource(id = R.color.icon_color))
            }
            if (uLike != 0){
                Text(text = standardLikes(uLike),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W500,
                    color = if(checked) colorResource(id = R.color.cred) else colorResource(id = R.color.icon_color),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .offset(x = (-7).dp),
                    fontSize = 16.sp)
            }
            if(!inMessenger){
                IconButton(onClick = {
                    startComments = true
                }) {
                    Icon(painterResource(id=R.drawable.comments), "Comments",
                        tint = colorResource(id = R.color.icon_color))
                }
            }

            IconButton(onClick = {
                if (exclusive) sendToMessenger(idPost, text, nameGroup, iconGroup, typeGroup,
                    existsImages, images, originalUrl, like, true, context)
                else showBottomSheet = true
            }) {
                Icon(painterResource(id=R.drawable.repost), "Repost",
                    tint = colorResource(id = R.color.icon_color))
            }
        }
        if(showBottomSheet){
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState) {
                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    Column() {
                        Text(text = nameGroup,
                            fontFamily = nunitoFamily,
                            fontWeight = FontWeight.W600,
                            color = colorResource(id = R.color.white))
                        if(text.length > 27){
                            Text(text = text.substring(0, 27) + "...",
                                fontFamily = nunitoFamily,
                                fontWeight = FontWeight.W600,
                                color = colorResource(id = R.color.white))
                        }
                        else{
                            Text(text = text,
                                fontFamily = nunitoFamily,
                                fontWeight = FontWeight.W600,
                                color = colorResource(id = R.color.white))
                        }

                    }
                    Spacer(modifier = Modifier.weight(0.7f))
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(originalUrl))
                    }) {
                        Icon(painterResource(id = R.drawable.copy), null,
                            tint = colorResource(id = R.color.icon_color))
                    }
                    IconButton(onClick = {
                        sendToMessenger(idPost, text, nameGroup, iconGroup, typeGroup, existsImages,
                            images, originalUrl, like, exclusive, context)
                    }) {
                        Icon(painterResource(id = R.drawable.repost), null,
                            tint = colorResource(id = R.color.icon_color))
                    }
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.05f))
            }
        }
        if(startComments){ //Отобразить ветку комментариев
            //LOAD COMMENTS FROM SERVER
            val comments = remember { listOf<Comments>().toMutableStateList() }
            Column(modifier = Modifier
                .fillMaxWidth(1f)) {
                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    IconButton(onClick = {
                        startComments = false
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Icon(painter = painterResource(id = R.drawable.arrow_down),
                            contentDescription = null)
                    }
                    Text(text = stringResource(id = R.string.comments),
                        fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                        fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterVertically))
                }
                ShowComments(comments = comments, clipboardManager)
                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 10.dp, end = 10.dp)) {
                    //Ввод комментариев
                    var comment by remember { mutableStateOf("") }

                    TextField(value = comment, onValueChange = { comment = it },
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .onFocusChanged { showBars.value = !it.isFocused },
                        placeholder = {
                            Text(text = stringResource(id = R.string.my_opinion),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 3.dp)
                                    .alpha(0.5f),
                                textAlign = TextAlign.Start,
                                color = colorResource(id = R.color.white))
                        },
                        leadingIcon = {
                            if(past.exists()) AsyncImage(model = BitmapFactory.decodeFile(past.absolutePath),
                                contentDescription = null, contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.CenterVertically)
                                    .clip(RoundedCornerShape(30.dp)))
                            else Icon(painter = painterResource(id = R.drawable.account_circle), contentDescription = null,
                                modifier = Modifier.size(30.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                coroutine.launch {
                                    //Отправка комментария
                                    comment = ""
                                }
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.send2), null,
                                    modifier = Modifier
                                        .padding(end = 7.dp)
                                        .size(28.dp))
                            }
                        },
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
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShowComments(comments: SnapshotStateList<Comments>,
                         clipboardManager: ClipboardManager){ //Отобразить комментарии
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)) {
        for(item in comments){
            val checked2 by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Text(text = item.author,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp)
                Text(text = item.text,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    modifier = Modifier.combinedClickable {
                        //На удержании, копируем текст в оперативную память
                        clipboardManager.setText(AnnotatedString(
                            "[${item.id}|${item.author}: ${getCurrentTimeStamp(item.time)}]\n${item.text}"))
                    })
                Row(modifier = Modifier
                    .fillMaxWidth(1f)) {
                    Button(onClick = { /*REPLY*/ },
                        modifier = Modifier.weight(0.2f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )) {
                        Text(text = stringResource(id = R.string.reply),
                            fontFamily = interFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp)
                    }
                    IconButton(onClick = { /*LIKE COMMENT*/ },
                        modifier = Modifier.weight(0.8f)) {
                        Icon(painterResource(id=R.drawable.like), "Like",
                            tint = if(checked2) colorResource(id = R.color.cred) else colorResource(id = R.color.icon_color))
                    }
                }

            }
        }
    }
}

private fun standardLikes(like: Int): String{
    if(like <= 999) return like.toString()
    else if(like <= 9999){
        val value = like.toString()
        return if (value[1] == '0') value[0] + "K"
        else value[0] + "." + value[1] + "K"
    }
    else if(like <= 99999) return (like.toString().substring(0, 2) + "K")
    return like.toString()
}

private fun sendToMessenger(idPost: Int, text: String, nameGroup: String, iconGroup: String,
                            typeGroup: String, existsImages: Boolean = false, images: VkImageAndVideo,
                            originalUrl: String, like: Int, exclusive: Boolean, context: Context
){
    copyPost = CopyPost(idPost, text, nameGroup, iconGroup, typeGroup, existsImages, images,
        originalUrl, like, exclusive)
    context.startActivity(Intent(context, PostToMessenger::class.java))
}

@Composable
fun HashtagsMentionsTextView(text: String, modifier: Modifier = Modifier, onClick: (String) -> Unit,
                             fontFamily: FontFamily, fontWeight: FontWeight) {

    val colorScheme = MaterialTheme.colorScheme
    val textStyle = SpanStyle(color = colorScheme.onBackground, fontFamily = fontFamily, fontWeight = fontWeight,
        fontSize = 17.sp)
    val primaryStyle = SpanStyle(color = colorResource(id = R.color.hashtag), fontFamily = fontFamily, fontWeight = fontWeight,
        fontSize = 17.sp)

    val hashtags = Regex("((?=[^\\w!])[#@][\\u4e00-\\u9fa5\\w]+)")

    val annotatedStringList = remember {

        var lastIndex = 0
        val annotatedStringList = mutableStateListOf<AnnotatedString.Range<String>>()

        for (match in hashtags.findAll(text)) {
            val start = match.range.first
            val end = match.range.last + 1
            val string = text.substring(start, end)
            if (start > lastIndex) {
                annotatedStringList.add(
                    AnnotatedString.Range(
                        text.substring(lastIndex, start),
                        lastIndex,
                        start,
                        "text"
                    )
                )
            }
            annotatedStringList.add(
                AnnotatedString.Range(string, start, end, "link")
            )
            lastIndex = end
        }

        if (lastIndex < text.length) {
            annotatedStringList.add(
                AnnotatedString.Range(
                    text.substring(lastIndex, text.length),
                    lastIndex,
                    text.length,
                    "text"
                )
            )
        }
        annotatedStringList
    }
    val annotatedString = buildAnnotatedString {
        annotatedStringList.forEach {
            if (it.tag == "link") {
                pushStringAnnotation(tag = it.tag, annotation = it.item)
                withStyle(style = primaryStyle) { append(it.item) }
                pop()
            } else {
                withStyle(style = textStyle) { append(it.item) }
            }
        }
    }
    ClickableText(
        text = annotatedString,
        style = TextStyle(fontFamily = fontFamily, fontWeight = fontWeight),
        modifier = modifier,
        onClick = { position ->
            val annotatedStringRange =
                annotatedStringList.first { it.start < position && position < it.end }
            if (annotatedStringRange.tag == "link") onClick(annotatedStringRange.item)
        }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostBase3(idPost: Int, text: String, nameGroup: String, iconGroup: String,
              typeGroup: String, existsImages: Boolean = true, images: VkImageAndVideo,
              originalUrl: String, like: Int, exclusive: Boolean, inMessenger: Boolean = false,
              myMessage: Boolean = true){
    val likesDatabase = LikesDatabase()
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState { images.images.size }
    var checked by remember { mutableStateOf(false) }

    coroutine.launch {
        val previewChecked = likesDatabase.getLikedByOriginalUrl(originalUrl)
        if (previewChecked != null){
            if(previewChecked == true){
                checked = true
            }
        }
    }

    Column(modifier= Modifier
        .padding(bottom = 7.dp)
        .fillMaxWidth(0.8f)
        .fillMaxHeight(0.8f)
        .background(
            color = if (myMessage) colorResource(id = R.color.my_message_color) else colorResource(
                id = R.color.message_color
            )
        )) {
        Spacer(modifier = Modifier.padding(top = 21.dp))
        Row(Modifier.padding(start = 10.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconGroup)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.preview),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = nameGroup, color = colorResource(id = R.color.white),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                    lineHeight = 20.sp
                )
                Text(
                    text = typeGroup, color = colorResource(id = R.color.type_group),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                    modifier = Modifier.offset(y = (-4).dp)
                )
            }
        }
        HashtagsMentionsTextView(
            text = text,
            onClick = {},
            modifier = Modifier.padding(bottom = 4.dp, start = 10.dp, end = 10.dp),
            fontFamily = nunitoFamily,
            fontWeight = FontWeight.W500
        )
        if (existsImages && images.images.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images.images[0])
                    .crossfade(true)
                    .build(),

                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

        }
    }
}

//DEPRECATED
//@Composable
//private fun ShowCurrentNumber(currentNumber: Int, maxNumbers: Int){
//    if(currentNumber > 0){
//        Box(modifier = Modifier
//            .background(
//                color = colorResource(id = R.color.background_field),
//                RoundedCornerShape(20.dp)
//            )
//            .padding(top = 10.dp, end = 10.dp),
//            contentAlignment = Alignment.Center) {
//            Text(text = "${currentNumber+1} / $maxNumbers",
//                modifier = Modifier.align(Alignment.Center))
//        }
//    }
//
//}