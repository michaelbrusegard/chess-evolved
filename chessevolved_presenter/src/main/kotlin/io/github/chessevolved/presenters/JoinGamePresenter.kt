package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.views.JoinGameView

class JoinGamePresenter(
    private val joinGameView: JoinGameView,
    private val navigator: Navigator,
) : IPresenter {
    init {
        joinGameView.init()
        joinGameView.onReturnButtonClicked = { navigator.goBack() }
        joinGameView.onJoinButtonClicked = { lobbyId -> joinGame(lobbyId) }
    }

    private fun joinGame(lobbyId: String) {
        val success = Lobby.joinLobby(lobbyId)

        if (success) {
            navigator.navigateToLobby(lobbyId)
        } else {
            joinGameView.showJoinError("Error message should be put here")
        }
    }

    override fun update(dt: Float) {
    }

    override fun render(sb: SpriteBatch) {
        joinGameView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        joinGameView.resize(width, height)
    }

    override fun dispose() {
        joinGameView.dispose()
    }

    override fun setInputProcessor() {
        joinGameView.setInputProcessor()
    }
}
