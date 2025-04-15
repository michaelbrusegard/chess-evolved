package io.github.chessevolved.presenters

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
            if (Game.getWantsRematch()) {
                // TODO: Navigate back to rematch-lobby and tell the other player that they too want a rematch. Could do this by setting wants-rematch back to false.
            } else {
                // TODO: Update text that the other player wants rematch.
            }
        }
        if (gameRow.player_disconnected) {
            // TODO: Disable rematch button.
        }
    }
}
