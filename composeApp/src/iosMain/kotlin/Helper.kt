import com.cheesecake.mafia.di.networkModule
import com.cheesecake.mafia.di.repositoryModule
import com.cheesecake.mafia.di.viewModelModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(networkModule(), repositoryModule(), viewModelModule())
    }
}