package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.singletons.Game
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler
import io.github.chessevolved.views.EndGameView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EndGamePresenter(
    private val endGameView: EndGameView,
    private val endGameStatus: Boolean,
    private val navigator: Navigator,
) : IPresenter {
    init {
        Game.subscribeToGameUpdates(this.toString(), ::onGameRowUpdate)
        endGameView.endGameStatus = endGameStatus
        endGameView.init()
        endGameView.onReturnToMenuClicked = { returnToMenu() }
        endGameView.onRematchClicked = { requestRematch() }
    }

    private fun requestRematch() {
        endGameView.disableRematchButton()
        endGameView.updateRematchText("Rematch request sent...")
        runBlocking {
            launch {
                Game.askForRematch()
            }
        }
    }

    private fun returnToMenu() {
        runBlocking {
            launch {
                Game.leaveGame()
                Lobby.leaveLobby()
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
        endGameView.dispose()
        Game.unsubscribeFromGameUpdates(this.toString())
    }

    override fun setInputProcessor() {
        endGameView.setInputProcessor()
    }

    fun onGameRowUpdate(gameRow: SupabaseGameHandler.Game) {
        if (gameRow.want_rematch) {
            if (!Game.getWantsRematch()) {
                endGameView.updateRematchText("The other player requests a rematch.")
            }
        }

        // If want_rematch gets set back to false, it's an ACK from the other player, accepting the rematch.
        if (!gameRow.want_rematch && Game.getWantsRematch()) {
            Gdx.app.postRunnable {
                navigator.navigateToLobby(Game.getGameId()!!)
            }
        }

        if (gameRow.player_disconnected) {
            endGameView.disableRematchButton()
            endGameView.updateRematchText("Other player has left...")
        }
    }
}
