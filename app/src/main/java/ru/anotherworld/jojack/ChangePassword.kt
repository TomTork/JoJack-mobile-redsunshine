package ru.anotherworld.jojack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class ChangePassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            JoJackTheme {
                ChangePass()
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePass(){
    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)) {
        TopAppBar(title = {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .background(colorResource(id = R.color.background2))
                .align(Alignment.CenterHorizontally)) {
                IconButton(
                    onClick = { context.startActivity(Intent(context, MainApp::class.java)) },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        painterResource(id = R.drawable.arrow_back), null,
                        tint = colorResource(id = R.color.white)
                    )
                }
                Text(
                    text = stringResource(id = R.string.change_password),
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600
                )

            }
        }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
            colors = TopAppBarDefaults
                .topAppBarColors(
                    containerColor = colorResource(id = R.color.background2),
                    titleContentColor = colorResource(id = R.color.background2)
                ))
        Column(modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .padding(bottom = 40.dp)
            .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center) {
            TextField(value = oldPassword,
                onValueChange = { oldPassword = it },
                placeholder = { Text(text = stringResource(id = R.string.old_password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 3.dp)
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.white)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(id = R.color.background_field),
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                modifier = Modifier
                    .padding(start = 19.dp, end = 19.dp)
                    .fillMaxWidth(1f))
            TextField(value = newPassword, onValueChange = { newPassword = it },
                placeholder = { Text(text = stringResource(id = R.string.new_password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 3.dp)
                        .alpha(0.5f),
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.white)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = colorResource(id = R.color.background_field),
                    cursorColor = Color.White,
                    disabledLabelColor = colorResource(id = R.color.ghost_white),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                modifier = Modifier
                    .padding(start = 19.dp, end = 19.dp, top = 20.dp)
                    .fillMaxWidth(1f))
            ElevatedButton(onClick = {
                if(oldPassword == newPassword) Toast.makeText(context,
                    context.getText(R.string.passwords_eq), Toast.LENGTH_SHORT).show()
                else{
                    //Сменить пароль
                }
            },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 10.dp, end = 10.dp, top = 30.dp)
                    .fillMaxWidth(0.9f)
                    .alpha(if (oldPassword != "" && newPassword != "") 1f else 0.8f),
                colors = ButtonDefaults
                    .buttonColors(containerColor = colorResource(id = R.color.background_lr_button)),
                shape = RoundedCornerShape(20.dp)) {
                Text(text = stringResource(id = R.string.change_password),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    color = colorResource(id = R.color.white)
                )
            }

        }
    }
} 