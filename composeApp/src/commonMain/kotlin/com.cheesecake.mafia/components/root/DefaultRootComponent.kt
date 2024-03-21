package com.cheesecake.mafia.components.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.cheesecake.mafia.components.finishedGame.DefaultFinishedGameComponent
import com.cheesecake.mafia.components.finishedGame.FinishedGameComponent
import com.cheesecake.mafia.state.NewGamePlayerItem
import com.cheesecake.mafia.components.liveGame.DefaultLiveGameComponent
import com.cheesecake.mafia.components.liveGame.LiveGameComponent
import com.cheesecake.mafia.components.main.DefaultMainComponent
import com.cheesecake.mafia.components.main.MainComponent
import com.cheesecake.mafia.components.newGame.DefaultNewGameComponent
import com.cheesecake.mafia.components.newGame.NewGameComponent
import com.cheesecake.mafia.state.FinishedGameProtocolState
import com.cheesecake.mafia.state.StartGameData
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Main,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, childComponentContext: ComponentContext): RootComponent.Child {
        return when (config) {
            is Config.Main -> {
                RootComponent.Child.Main(mainComponent(childComponentContext))
            }
            is Config.NewGame -> {
                RootComponent.Child.NewGame(newGameComponent(childComponentContext))
            }
            is Config.LiveGame -> RootComponent.Child.LiveGame(
                liveGameComponent(childComponentContext, config.data)
            )
            is Config.FinishedGame -> RootComponent.Child.FinishedGame(
                finishedGameComponent(childComponentContext, config.protocol)
            )
        }
    }

    private fun mainComponent(componentContext: ComponentContext): MainComponent =
        DefaultMainComponent(
            componentContext = componentContext,
            onStartNewGame = { navigation.push(Config.NewGame) }
        )

    private fun newGameComponent(componentContext: ComponentContext): NewGameComponent =
        DefaultNewGameComponent(
            componentContext = componentContext,
            onStartNewGame = {
                navigation.push(Config.LiveGame(it))
            },
            onBackPressed = { navigation.pop() },
        )

    private fun liveGameComponent(
        componentContext: ComponentContext,
        data: StartGameData,
    ): LiveGameComponent =
        DefaultLiveGameComponent(
            componentContext = componentContext,
            data = data,
            onFinishGame = { protocol ->
                navigation.popTo(0)
                navigation.push(Config.FinishedGame(protocol))
            }
        )

    private fun finishedGameComponent(
        componentContext: ComponentContext,
        protocol: FinishedGameProtocolState,
    ): FinishedGameComponent =
        DefaultFinishedGameComponent(
            componentContext = componentContext,
            protocol = protocol,
            onBackPressedClicked = { navigation.popTo(0) }
        )

    override fun onBackClicked(toIndex: Int) {
        navigation.popTo(index = toIndex)
    }

    @Serializable
    private sealed class Config {
        @Serializable
        data object Main : Config()

        @Serializable
        data object NewGame : Config()

        @Serializable
        data class LiveGame(val data: StartGameData) : Config()

        @Serializable
        data class FinishedGame(val protocol: FinishedGameProtocolState) : Config()
    }
}