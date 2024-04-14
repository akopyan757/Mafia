import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.cheesecake.mafia.common.ProjectTheme
import com.cheesecake.mafia.components.root.DefaultRootComponent
import com.cheesecake.mafia.data.GameActionType
import com.cheesecake.mafia.data.LiveStage
import com.cheesecake.mafia.di.databaseModule
import com.cheesecake.mafia.di.networkModule
import com.cheesecake.mafia.di.repositoryModule
import com.cheesecake.mafia.di.viewModelModule
import com.cheesecake.mafia.ui.root.RootAdminScreen
import com.cheesecake.mafia.ui.second.InteractiveScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.compose.KoinApplication


@OptIn(ExperimentalDecomposeApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    val root = runBlocking(Dispatchers.Main) {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }

    application {
        val windowFocusRequestSharedFlow = remember { MutableSharedFlow<WindowType>() }

        KoinApplication(application = {
            modules(networkModule(), databaseModule(), repositoryModule(), viewModelModule())
        }) {
            WindowType.entries.forEach { windowType ->
                val windowState = rememberWindowState()
                LifecycleController(lifecycle, windowState)
                key(windowType) {
                    Window(
                        onCloseRequest = ::exitApplication,
                        state = windowState,
                        title = "Mafia"
                    ) {
                        LaunchedEffect(Unit) {
                            windowFocusRequestSharedFlow
                                .filter { it == windowType }
                                .collect {
                                    window.toFront()
                                }
                        }

                        ProjectTheme {
                            if (windowType == WindowType.First) {
                                RootAdminScreen(component = root)
                            } else if (windowType == WindowType.Second) {
                                InteractiveScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    val lifecycle = LifecycleRegistry()
    val root = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
    )
    RootAdminScreen(component = root)
}