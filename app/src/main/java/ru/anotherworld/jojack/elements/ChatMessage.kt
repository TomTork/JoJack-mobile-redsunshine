package ru.anotherworld.jojack.elements

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.R

@Composable
fun ChatMessage(name: String, previewMessage: String, username: String, image: ImageBitmap? = null,
                countMessage: Int = 0) {
    Column(modifier = Modifier
        .padding(start = 5.dp, end = 5.dp, top = 5.dp)
        .fillMaxWidth(1f)) {
        Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.alpha(0.2f))
        Row {
            if(image == null){
                Icon(
                    painterResource(id = R.drawable.account_circle), null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 10.dp))
            }
            else{
                Icon(image, null, Modifier
                    .size(40.dp)
                    .padding(start = 10.dp))
            }
            Text(text = name, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp), style=MaterialTheme.typography.titleLarge)
            if (countMessage != 0){
                Text(text = countMessage.toString(), modifier = Modifier
                    .background(colorResource(id = R.color.grey), shape= RoundedCornerShape(20.dp))
                    .align(Alignment.CenterVertically))
            }
        }
        Text(text = "$username: $previewMessage", modifier = Modifier.padding(start=10.dp,
            bottom = 5.dp), style=MaterialTheme.typography.bodyLarge)

    }
}