package ru.anotherworld.jojack.elements

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun PostBase2(idPost: Int, text: String, nameGroup: String, iconGroup: String,
              typeGroup: String, existsImages: Boolean = false, images: VkImageAndVideo,
              originalUrl: String, like: Int){
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    val context = LocalContext.current
    val likeController = LikeController()
    var checked by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    Column(modifier= Modifier
        .padding(bottom = 7.dp)
        .fillMaxWidth(1f)
        .background(color = colorResource(id = R.color.black2))) {
        Spacer(modifier=Modifier.padding(top=21.dp))
        Row(Modifier.padding(start=21.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconGroup)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.preview),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp)
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
        Text(text=text, fontFamily=nunitoFamily, fontWeight=FontWeight.W500,
            modifier=Modifier.padding(bottom=4.dp, start=21.dp, end=21.dp), lineHeight=20.sp)
        if(existsImages && images.images.size > 1){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images.images[0])
                    .crossfade(true)
                    .build(),

                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Row(modifier=Modifier.padding(top=4.dp, start=10.dp, end=21.dp)) {
            IconButton(onClick = {
                coroutine.launch {
                    likeController.newLike(originalUrl, !checked)
                    checked = !checked
                }
            }) {
                Icon(painterResource(id=R.drawable.like), "Like",
                    tint = colorResource(id = R.color.icon_color))
            }
            IconButton(onClick = {  }) {
                Icon(painterResource(id=R.drawable.comments), "Comments",
                    tint = colorResource(id = R.color.icon_color))
            }
            IconButton(onClick = {  }) {
                Icon(painterResource(id=R.drawable.repost), "Repost",
                    tint = colorResource(id = R.color.icon_color))
            }
        }

    }
}
