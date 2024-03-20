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
import com.cheesecake.mafia.components.root.DefaultRootComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalDecomposeApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    val root = runBlocking(Dispatchers.Main) {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }
    application {
        val windowState = rememberWindowState()

        LifecycleController(lifecycle, windowState)

        val windowFocusRequestSharedFlow = remember { MutableSharedFlow<WindowType>() }
        WindowType.entries.forEach { windowType ->
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

                    App(component = root)
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
    App(root)
}