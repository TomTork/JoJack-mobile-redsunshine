package ru.anotherworld.jojack.elements

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ShapeDrawable
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import ru.anotherworld.jojack.chatcontroller.getCurrentTimeStamp
import ru.anotherworld.jojack.database.Comments
import ru.anotherworld.jojack.mDatabase

val constructorMessenger = ConstructorMessenger(null, null, null, null,
    null, null, null, null, null, null, null)

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PostBase2(idPost: Int, text: String, nameGroup: String, iconGroup: String,
              typeGroup: String, existsImages: Boolean = false, images: VkImageAndVideo,
              originalUrl: String, like: Int, exclusive: Boolean, inMessenger: Boolean = false){
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    val interFamily = FontFamily(
        Font(R.font.inter600, FontWeight.W600),
        Font(R.font.inter_medium500, FontWeight.W500)
    )
    val likesDatabase = LikesDatabase()
    val context = LocalContext.current
    val likeController = LikeController()
    val coroutine = rememberCoroutineScope()
    val pagerState = rememberPagerState { images.images.size }
    var checked by remember { mutableStateOf(false) }
    var startComments by remember { mutableStateOf(false) }

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
        .background(color = colorResource(id = R.color.black2))) {
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images.images[index])
                        .crossfade(true)
                        .build(),

                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
                    //DEPRECATED
//                    constructorMessenger.idPost = idPost
//                    constructorMessenger.text = text
//                    constructorMessenger.nameGroup = text
//                    constructorMessenger.iconGroup = text
//                    constructorMessenger.typeGroup = text
//                    constructorMessenger.images = images
//                    constructorMessenger.originalUrl = originalUrl
//                    constructorMessenger.like = like
//                    constructorMessenger.exclusive = exclusive
//                    constructorMessenger.inMessenger = true
//                    context.startActivity(Intent(context, MessengerPostActivity::class.java))
                }) {
                    Icon(painterResource(id=R.drawable.comments), "Comments",
                        tint = colorResource(id = R.color.icon_color))
                }
            }

            IconButton(onClick = {
                if (exclusive) sendToMessenger(idPost, text, nameGroup, iconGroup, typeGroup,
                    existsImages, images, originalUrl, like, exclusive)
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
                    Column {
                        Text(text = nameGroup,
                            fontFamily = nunitoFamily,
                            fontWeight = FontWeight.W600,
                            color = colorResource(id = R.color.white))
                        Text(text = text.substring(0, 27) + "...",
                            fontFamily = nunitoFamily,
                            fontWeight = FontWeight.W600,
                            color = colorResource(id = R.color.white))
                    }
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(originalUrl))
                    }) {
                        Icon(painterResource(id = R.drawable.copy), null,
                            tint = colorResource(id = R.color.icon_color))
                    }
                    IconButton(onClick = {
                        sendToMessenger(idPost, text, nameGroup, iconGroup, typeGroup, existsImages,
                            images, originalUrl, like, exclusive)
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
            val checked2 by remember { mutableStateOf(false) }
//            val lazyScroll = rememberLazyListState()
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
//                LazyColumn{
//
//                    itemsIndexed(comments){ _, item ->
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth(1f)
//                                .padding(start = 10.dp, end = 10.dp)
//                        ) {
//                            Text(text = item.author,
//                                fontFamily = interFamily,
//                                fontWeight = FontWeight.W600,
//                                fontSize = 18.sp)
//                            Text(text = item.text,
//                                fontFamily = interFamily,
//                                fontWeight = FontWeight.W600,
//                                fontSize = 16.sp,
//                                modifier = Modifier.combinedClickable {
//                                    //На удержании, копируем текст в оперативную память
//                                    clipboardManager.setText(AnnotatedString(
//                                        "[${item.id}|${item.author}: ${getCurrentTimeStamp(item.time)}]\n${item.text}"))
//                                })
//                            Row(modifier = Modifier
//                                .fillMaxWidth(1f)) {
//                                Button(onClick = { /*REPLY*/ },
//                                    modifier = Modifier.weight(0.2f),
//                                    colors = ButtonDefaults.buttonColors(
//                                        containerColor = Color.Transparent
//                                    )) {
//                                    Text(text = stringResource(id = R.string.reply),
//                                        fontFamily = interFamily,
//                                        fontWeight = FontWeight.W600,
//                                        fontSize = 16.sp)
//                                }
//                                IconButton(onClick = { /*LIKE COMMENT*/ },
//                                    modifier = Modifier.weight(0.8f)) {
//                                    Icon(painterResource(id=R.drawable.like), "Like",
//                                        tint = if(checked2) colorResource(id = R.color.cred) else colorResource(id = R.color.icon_color))
//                                }
//                            }
//
//                        }
//                    }
//                }
                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(start = 10.dp, end = 10.dp)) {
                    //Ввод комментариев
                    var data = remember { byteArrayOf() }
                    var comment by remember { mutableStateOf("") }
                    coroutine.launch {
                        val now = mDatabase.getIcon()
                        if(now != null) data = now
                    }

                    TextField(value = comment, onValueChange = { comment = it },
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
                            if(data.isNotEmpty()) Icon(bitmap = BitmapFactory.decodeByteArray(data, 0, data.size).asImageBitmap(),
                                null, modifier = Modifier.size(30.dp))
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
                            originalUrl: String, like: Int, exclusive: Boolean){

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