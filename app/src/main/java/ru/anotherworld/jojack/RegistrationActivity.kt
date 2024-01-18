package ru.anotherworld.jojack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.anotherworld.jojack.ui.theme.JoJackTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JoJackTheme {

                RegistrationContent()
            }
        }
    }
}

@Composable
private fun RegistrationContent(){
    var login by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    Column(modifier = Modifier
        .background(colorResource(id = R.color.cred))
        .fillMaxSize(1f)) {
        Text(
            text = "JoJack",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
        Column(modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(value = login,
                label = { Text(text = stringResource(id = R.string.login),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold) },
                onValueChange = { login = it },
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp)
                    .shadow(3.dp)
                    .fillMaxWidth(0.9f))
            OutlinedTextField(value = password,
                label = { Text(text = stringResource(id = R.string.password),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold) },
                onValueChange = { password = it },
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                    .shadow(3.dp)
                    .fillMaxWidth(0.9f))
            ElevatedButton(onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 40.dp, end = 40.dp, top = 10.dp),
            ) {
                Text(text = stringResource(id = R.string.register),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    color = colorResource(id = R.color.white)
                )
            }
        }

        Column(verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(1f)
                .padding(bottom = 10.dp)
                .fillMaxWidth(1f)) {
            Text(text = stringResource(id = R.string.to_enter),
                style = MaterialTheme.typography.labelLarge,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                .align(Alignment.CenterHorizontally)
            )
        }
    }
}