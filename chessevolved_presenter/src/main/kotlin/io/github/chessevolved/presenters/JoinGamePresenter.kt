package io.github.chessevolved.presenters

import LobbyPresenter
import io.github.chessevolved.ScenePresenterStateManager
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.views.JoinGameView
import io.github.chessevolved.views.LobbyView

class JoinGamePresenter(
    private val view: JoinGameView,
) : IPresenter {
    init {
        view.init()
        view.onReturnButtonClicked = { returnToMenu() }
        view.onJoinButtonClicked = {
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
            ScenePresenterStateManager.push(StatePresenter(lobbyPresenter))
        } else {
            view.showJoinError("Error message should be put here")
        }
    }

    /**
     * Return to the menu scene using the presenter manager??
     */
    fun returnToMenu() {
        ScenePresenterStateManager.pop()
    }

    override fun render() {
        view.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        view.resize(width, height)
    }

    override fun dispose() {
        view.dispose()
    }

    override fun setInputProcessor() {
        view.setInputProcessor()
    }
}
