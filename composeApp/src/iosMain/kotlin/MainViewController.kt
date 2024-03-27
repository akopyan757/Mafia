import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.cheesecake.mafia.common.ProjectTheme
import com.cheesecake.mafia.components.root.DefaultRootComponent
import com.cheesecake.mafia.ui.root.RootUserScreen

fun MainViewController() = ComposeUIViewController {
    val lifecycle = LifecycleRegistry()
    val root = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
    )
    ProjectTheme {
        RootUserScreen(root)
    }
}
