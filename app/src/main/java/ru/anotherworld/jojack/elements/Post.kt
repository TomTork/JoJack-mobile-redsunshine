package ru.anotherworld.jojack.elements

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.R
import java.time.format.TextStyle


@Composable
fun PostBase(
    idPost: Int, text: String, nameGroup: String = "Simple TEXT",
    existsImage: Boolean = false, image: ImageBitmap? = null){
    Column(modifier = Modifier
        .padding(start = 5.dp, end = 5.dp, top = 5.dp)
        .fillMaxWidth(1f)) {
        Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.alpha(0.2f))
        Row {
            Icon(
                painterResource(id = R.drawable.account_circle), null,
                modifier = androidx.compose.ui.Modifier
                    .size(40.dp)
                    .padding(start = 10.dp))
            Text(text = "$nameGroup [$idPost]", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp), style=MaterialTheme.typography.titleLarge)
        }
        Text(text = text,
            modifier = Modifier.padding(start=10.dp, bottom = 5.dp), lineHeight = 15.sp,
            style = MaterialTheme.typography.bodyLarge
        )
        if(existsImage){
            Image(image!!, "simple image", modifier = Modifier
                .background(colorResource(id = R.color.white), shape = RoundedCornerShape(10.dp))
                .align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun PostBase2(idPost: Int, text: String, nameGroup: String, iconGroup: ImageBitmap,
              typeGroup: String, existsImages: Boolean = false, images: List<ImageBitmap>? = null){
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    Column(modifier=Modifier.padding(top=25.dp, bottom=26.dp, start=21.dp, end=21.dp)) {
        Row {
            Image(iconGroup, null)
            Column {
                Text(text=nameGroup, color=colorResource(id=R.color.white),
                    fontFamily=nunitoFamily, fontWeight=FontWeight.W600)
                Text(text=typeGroup, color=colorResource(id=R.color.type_group),
                    fontFamily=nunitoFamily, fontWeight=FontWeight.W600)
            }
        }
        Text(text=text, fontFamily=nunitoFamily, fontWeight=FontWeight.W500,
            modifier=Modifier.padding(bottom=4.dp))
        if(existsImages){
            Image(images!![0], null,
                modifier=Modifier.clip(RoundedCornerShape(8.dp)))
        }
        Row(modifier=Modifier.padding(top=4.dp)) {
            IconButton(onClick = {  }) {
                Icon(painterResource(id=R.drawable.like), "Like")
            }
            IconButton(onClick = {  }) {
                Icon(painterResource(id=R.drawable.comments), "Comments")
            }
            IconButton(onClick = {  }) {
                Icon(painterResource(id=R.drawable.repost), "Repost")
            }
        }

    }
}
