package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.dtos.GameDto
import io.github.chessevolved.singletons.Game
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.views.EndGameView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EndGamePresenter(
    private val endGameView: EndGameView,
    private val endGameStatus: Boolean,
    private val navigator: Navigator,
) : IPresenter {
    private var otherPlayerHasAskedForRematch = false
    private var otherPlayerLeft = false

    init {
        println("${Game.isInGame()} init")
        Game.subscribeToGameUpdates(this.toString(), ::onGameUpdate)
        endGameView.endGameStatus = endGameStatus
        endGameView.init()
        endGameView.onReturnToMenuClicked = { returnToMenu() }
        endGameView.onRematchClicked = { requestRematch() }
    }

    private fun requestRematch() {
        println("${Game.isInGame()} rematch")
        endGameView.disableRematchButton()
        endGameView.updateRematchText("Rematch request\nsent...")
        runBlocking {
            launch {
                Game.askForRematch()
            }
        }
    }

    private fun returnToMenu() {
        println("${Game.isInGame()} returntomenu")
        runBlocking {
            launch {
                val wantsRematch = Game.getWantsRematch()
                Game.leaveGame()

                // This is to skip the edge-case where you have requested a rematch, which calls setupRematchLobby,
                // setting second_player = false in the lobby row. If you then try to leave it will cause an error cause the other player is still in the lobby.
                if (wantsRematch && !otherPlayerLeft) {
                    Lobby.leaveLobbyWithoutUpdating()
                } else {
                    Lobby.leaveLobby()
                }
            }
        }
        navigator.goBack()
    }

    override fun render(sb: SpriteBatch) {
        endGameView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        endGameView.resize(width, height)
    }

    override fun dispose() {
        println("${Game.isInGame()} dispose") // Denne e false.... Noe leavea gamen alt for tidlig
        endGameView.dispose()
        Game.unsubscribeFromGameUpdates(this.toString())

        // Both isInGame and isInLobby must be checked here
        // game and lobby is being left somewhere else leading to a fatal error if not checked
        runBlocking {
            launch {
                val wantsRematch = Game.getWantsRematch()
                if (Game.isInGame()) {
                    Game.leaveGame()
                }

                // TODO: This logic doesn't work as of yet. In further iterations, this would be priority.
                if (wantsRematch && !otherPlayerLeft) {
                    // TODO: Need to discern between a player that leaves the rematch-screen, and a player that goes from rematch-screen to a lobby.
                    // Lobby.leaveLobbyWithoutUpdating()
                } else {
                    if (Lobby.isInLobby()) {
                        // Lobby.leaveLobby()
                    }
                }
            }
        }
    }

    override fun setInputProcessor() {
        endGameView.setInputProcessor()
    }

    fun onGameUpdate(updatedGame: GameDto) {
        println("${Game.isInGame()} gameupdate")
        if (updatedGame.wantRematch) {
            if (!Game.getWantsRematch()) {
                otherPlayerHasAskedForRematch = true
                endGameView.updateRematchText("Other player\nwants a rematch.")
            }
        }

        // If want_rematch gets set back to false, it's an ACK from the other player, accepting the rematch.
        if (!updatedGame.wantRematch && Game.getWantsRematch()) {
            Gdx.app.postRunnable {
                // This ensures that the first one to request a rematch doesn't update the lobby-column "second_player". Needed to avoid a full lobby exception.
                if (otherPlayerHasAskedForRematch) {
                    runBlocking {
                        launch {
                            Game.leaveGame()
                            Lobby.joinRematchLobbyNonHost()
                        }
                    }
                } else {
                    runBlocking {
                        launch {
                            Game.deleteGame()
                            Lobby.joinRematchLobbyAsHost()
                        }
                    }
                }
                navigator.navigateToLobby(Lobby.getLobbyId()!!)
            }
        }

        if (updatedGame.playerDisconnected) {
            otherPlayerLeft = true
            endGameView.disableRematchButton()
            endGameView.updateRematchText("Other player\nhas left...")
        }
    }
}
