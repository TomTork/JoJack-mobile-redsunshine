package ru.anotherworld.jojack

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(){
    var output by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }
    var job by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val terminal = MTerminal()
    val array = remember { listOf<String>().toMutableStateList() }
    coroutine.launch { 
        if(mDatabase.getJob()!! >= 4) job = true
    }
    val state = rememberLazyListState()
    val cipher = Cipher()
    var password by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxHeight(1f)
        .fillMaxWidth(1f)
        .background(color = colorResource(id = R.color.black2))) {
        Scaffold(topBar = {
            TopAppBar(title = {
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
            }, modifier = Modifier.background(color = colorResource(id = R.color.background2)),
                colors = TopAppBarDefaults
                    .topAppBarColors(containerColor = colorResource(id = R.color.background2),
                        titleContentColor = colorResource(id = R.color.background2)))

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
                                if(!job &&
                                    (cipher.hash(command) == "58a7273cae5316586bbd412d8d15f7b74b8885974400b9a84453d1a4c497831d39d16552370a18f36454eb44c78e1b6eaacd84fd51b240872ceeba85311e4f5e") ||
                                    cipher.hash(command) == "8b53086ae560a67b0263f2ce6e9321b8afd28e01ce9001096f44a2ac6a4a8f0dea4d943783b7c307d2e95df9bf4fbc617f953abfd6446f242b710ce6d5a2648e"){
                                    password = cipher.encrypt(command, cipher.md52(command))
                                    array.add(context.getText(R.string.access_is_allowed).toString())
                                    job = true
                                }
                                else if(job){
                                    array.add(terminal.sendQuery(command, password))
                                    state.animateScrollToItem(array.size)
                                }
                                else{
                                    array.add(context.getText(R.string.no_permission).toString())
                                }
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
                    .background(color = colorResource(id = R.color.black2))
                    .padding(top = 40.dp, start = 10.dp, end = 10.dp, bottom = 80.dp),
                    state = state
                ){
                    if(!job){
                        item {
                            Text(text = stringResource(id = R.string.no_permission), modifier = Modifier
                                .padding(top = 30.dp)
                                .fillMaxWidth(1f)
                                .background(
                                    color = colorResource(id = R.color.black2),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                                fontFamily = nunitoFamily, fontWeight = FontWeight.W500)    
                        }
                    }

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