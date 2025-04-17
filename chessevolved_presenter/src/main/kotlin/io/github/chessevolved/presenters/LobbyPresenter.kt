package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.shared.SettingsDTO
import io.github.chessevolved.singletons.GameSettings
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.singletons.Lobby.getLobby
import io.github.chessevolved.singletons.Lobby.leaveLobby
import io.github.chessevolved.singletons.Lobby.startGame
import io.github.chessevolved.singletons.Lobby.subscribeToLobbyUpdates
import io.github.chessevolved.singletons.Lobby.unsubscribeFromLobbyUpdates
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import io.github.chessevolved.views.LobbyView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LobbyPresenter(
    private val lobbyView: LobbyView,
    private val navigator: Navigator,
) : IPresenter {
    private var lobbyStarted = false

    init {
        lobbyView.onLeaveButtonClicked = { returnToMenu() }
        lobbyView.onStartGameButtonClicked = { onStartGameButtonClicked() }
        lobbyView.onOpenSettingsButtonClicked = { navigator.navigateToSettings() }
        lobbyView.init()
        subscribeToLobbyUpdates(this.toString(), ::lobbyUpdateHandler)
        runBlocking {
            launch {
                val lobby = getLobby()
                lobbyUpdateHandler(lobby)
            }
        }
    }

    private fun playerJoinedLeftLobby(playerJoined: Boolean) {
        lobbyView.setSecondPlayerConnected(playerJoined)
    }

    private fun lobbyStartedCheck(lobbyStarted: Boolean) {
        if (lobbyStarted) {
            // Get back on main thread.
            Gdx.app.postRunnable {
                this.lobbyStarted = true
                navigator.navigateToGame()
            }
        }
    }

    private fun settingsChanged(updatedSettings: SettingsDTO) {
        GameSettings.setGameSettings(updatedSettings)
    }

    private fun lobbyUpdateHandler(newLobby: SupabaseLobbyHandler.Lobby) {
        val settingsDTO =
            SettingsDTO(
                fogOfWar = newLobby.settings["fogOfWar"]?.toBooleanStrictOrNull() ?: false,
                boardSize = newLobby.settings["boardSize"]?.toIntOrNull() ?: 8,
            )

        playerJoinedLeftLobby(newLobby.second_player)
        lobbyStartedCheck(newLobby.game_started)
        settingsChanged(settingsDTO)
    }

    private fun onStartGameButtonClicked() {
        if (lobbyStarted) return
        runBlocking {
            launch {
                try {
                    startGame()
                    lobbyStarted = true
                } catch (e: Exception) {
                    lobbyView.showError("Failure when starting game from presenter. " + e.message)
                }
            }
        }
    }

    /**
     * Change to MenuPresenter
     */
    private fun returnToMenu() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                leaveLobby()
            } catch (e: Exception) {
                println("Non fatal error: Problem with calling leaveLobby(). Error: " + e.message)
            }
        }
        unsubscribeFromLobbyUpdates(this.toString())
        navigator.goBack()
    }

    override fun render(sb: SpriteBatch) {
        lobbyView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        lobbyView.resize(width, height)
    }

    override fun dispose() {
        lobbyView.dispose()
        unsubscribeFromLobbyUpdates(this.toString())
        if (Lobby.isInLobby() && !lobbyStarted) {
            runBlocking {
                launch {
                    try {
                        leaveLobby()
                    } catch (e: Exception) {
                        println("Non fatal error: Problem with calling leaveLobby(). Error: " + e.message)
                    }
                }
            }
        }
    }

    override fun setInputProcessor() {
        lobbyView.setInputProcessor()
    }
}
