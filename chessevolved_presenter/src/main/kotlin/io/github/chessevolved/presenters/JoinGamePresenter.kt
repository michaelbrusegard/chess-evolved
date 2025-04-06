package io.github.chessevolved.presenters

import LobbyPresenter
import io.github.chessevolved.PresenterManager
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.views.JoinGameView
import io.github.chessevolved.views.LobbyView

class JoinGamePresenter(
    private val joinGameView: JoinGameView,
) : IPresenter {
    init {
        joinGameView.init()
        joinGameView.onReturnButtonClicked = { returnToMenu() }
        joinGameView.onJoinButtonClicked = {
            joinGame(
                lobbyId = it,
            )
        }
    }

    /**
     * Attempts to join a game lobby by ID
     *
     * @param lobbyID String representing the lobby to join
     * @return Boolean indicating success or failure
     */
    private fun joinGame(lobbyId: String) {
        val success = Lobby.joinLobby(lobbyId)

        if (success) {
            val lobbyPresenter = LobbyPresenter(LobbyView(lobbyId))
            PresenterManager.push(StatePresenter(lobbyPresenter))
        } else {
            joinGameView.showJoinError("Error message should be put here")
        }
    }

    /**
     * Return to the menu scene using the presenter manager??
     */
    fun returnToMenu() {
        PresenterManager.pop()
    }

    override fun render() {
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
