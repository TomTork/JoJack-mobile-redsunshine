package ru.anotherworld.jojack.elements

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.R


@Composable
fun PostBase(idPost: Int, text: String, nameGroup: String = "Simple TEXT",
             existsImage: Boolean = false, image: ImageBitmap? = null){
    Column(modifier = Modifier
        .padding(start = 5.dp, end = 5.dp, top = 5.dp)
        .background(colorResource(id = R.color.grey), shape = RoundedCornerShape(10.dp))
        .fillMaxWidth(1f)) {
        Row {
            Icon(
                painterResource(id = R.drawable.account_circle), null,
                modifier = androidx.compose.ui.Modifier.size(45.dp).padding(start=10.dp))
            Text(text = "$nameGroup [$idPost]", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start=5.dp))
        }
        if(existsImage){
            Image(image!!, "simple image")
        }
        Text(text = text, modifier = Modifier.padding(start=10.dp, bottom = 5.dp))
    }
}
