package com.cheesecake.mafia

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
    //Path [/Users/akopyanalbert/AndroidStudioProjects/Mafia/composeApp/src/commonMain/resources] of module
// [Mafia.composeApp.commonMain] was removed from modules [Mafia.composeApp.main]
}