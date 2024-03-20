import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.swing.SwingDispatcher

internal fun <T> runOnUiThread(block: () -> T): T {
    return runBlocking (Dispatchers.Swing) {
        block()
    }
}