package ru.anotherworld.jojack.elements

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
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
import kotlin.math.max

@Composable
fun PostBase2(idPost: Int, text: String, nameGroup: String, iconGroup: Painter? = null,
              typeGroup: String, existsImages: Boolean = false, images: List<Painter>? = null){
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    Column(modifier= Modifier
        .padding(bottom = 7.dp)
        .fillMaxWidth(1f)
        .background(color = colorResource(id = R.color.black2))) {
        Spacer(modifier=Modifier.padding(top=21.dp))
        Row(Modifier.padding(start=21.dp)) {
            Image(iconGroup!!, null, modifier= Modifier
                .size(30.dp)
                .align(Alignment.CenterVertically))
            Column(modifier=Modifier.padding(start=10.dp)) {
                Text(text=nameGroup, color=colorResource(id=R.color.white),
                    fontFamily=nunitoFamily, fontWeight=FontWeight.W600)
                Text(text=typeGroup, color=colorResource(id=R.color.type_group),
                    fontFamily=nunitoFamily, fontWeight=FontWeight.W600,
                    modifier=Modifier.offset(y=(-4).dp))
            }
        }
        Text(text=text, fontFamily=nunitoFamily, fontWeight=FontWeight.W500,
            modifier=Modifier.padding(bottom=4.dp, start=21.dp, end=21.dp), lineHeight=16.sp)
        if(existsImages){
            Image(images!![0], null,
                modifier= Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .padding(start = 21.dp, end = 21.dp)
                    .requiredSizeIn(minWidth = Dp.Infinity, minHeight = 100.dp),
                alignment = Alignment.Center)
        }
        Row(modifier=Modifier.padding(top=4.dp, start=10.dp, end=21.dp)) {
            IconButton(onClick = {  }) {
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
