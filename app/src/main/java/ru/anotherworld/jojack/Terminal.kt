package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class Terminal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            JoJackTheme {
                Content()
            }
        }
    }
}

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(){
    var output by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val nunitoFamily = FontFamily(
        Font(R.font.nunito_semibold600, FontWeight.W600),
        Font(R.font.nunito_medium500, FontWeight.W500)
    )
    val array = remember { listOf<String>().toMutableStateList() }
    Column(modifier = Modifier
        .fillMaxHeight(1f)
        .fillMaxWidth(1f)
        .background(color = colorResource(id = R.color.black2))) {
        Scaffold(topBar = {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .background(color = colorResource(id = R.color.background2))) {
                IconButton(onClick = {
                    context.startActivity(Intent(context, MainApp::class.java))
                }) {
                    Icon(painterResource(id = R.drawable.arrow_back), null,
                        tint = colorResource(id = R.color.white))
                }
                Text(text = stringResource(id = R.string.terminal),
                    fontFamily = nunitoFamily, fontWeight = FontWeight.W600,
                    fontSize = 27.sp, modifier = Modifier.align(Alignment.CenterVertically),
                    color = colorResource(id = R.color.white))
            }
        },
            modifier = Modifier.background(color = colorResource(id = R.color.black2)),
            bottomBar = {
                TextField(value = command, onValueChange = { command = it },
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                        .background(
                            color = colorResource(id = R.color.black2),
                        ),
                    maxLines = 1,
                    trailingIcon = {
                        IconButton(onClick = {
                            coroutine.launch {
                                //SEND RESULT TO ARRAY
                                command = ""
                            }
                        }) {
                            Icon(painterResource(id = R.drawable.send2), null,
                                modifier = Modifier
                                    .padding(end = 7.dp)
                                    .size(28.dp))
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = colorResource(id = R.color.background_field),
                        cursorColor = Color.White,
                        disabledLabelColor = colorResource(id = R.color.ghost_white),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        ))
            }) {
            Column(modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)) {
                LazyColumn(modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(1f)
                    .background(color = colorResource(id = R.color.black2))){
                    itemsIndexed(array){ index, item ->
                        Text(text = item, modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth(1f)
                            .background(
                                color = colorResource(id = R.color.black2),
                                shape = RoundedCornerShape(10.dp)
                            ),
                            fontFamily = nunitoFamily, fontWeight = FontWeight.W500)
                    }
                }

            }
        }
    }
}