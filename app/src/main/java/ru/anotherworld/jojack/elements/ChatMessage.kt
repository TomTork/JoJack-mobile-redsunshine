package ru.anotherworld.jojack.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.R

@Composable
fun ChatMessage(name: String, previewMessage: String, username: String, idChat: Int = 0,
                image: ImageBitmap? = null, countMessage: Int = 0, action: (id: Int) -> Unit) {
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    Column(modifier = Modifier
        .padding(top = 4.dp)
        .fillMaxWidth(1f)
        .background(colorResource(id = R.color.black2))
        .clickable { action(idChat) }) {
        Divider(color = colorResource(id = R.color.background2),
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
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
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