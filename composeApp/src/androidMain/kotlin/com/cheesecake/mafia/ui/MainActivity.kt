package com.cheesecake.mafia.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.cheesecake.mafia.common.ProjectTheme
import com.cheesecake.mafia.components.root.DefaultRootComponent
import com.cheesecake.mafia.ui.root.RootUserScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lifecycle = LifecycleRegistry()
        val root = DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
        setContent {
            ProjectTheme {
                RootUserScreen(root)
            }
        }
    }
}