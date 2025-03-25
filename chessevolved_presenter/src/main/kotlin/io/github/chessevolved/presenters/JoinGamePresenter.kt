package io.github.chessevolved.presenters

import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.views.JoinGameView

class JoinGamePresenter(
    private val view: JoinGameView,
) : IPresenter {
    init {
        view.init()
        view.onReturnButtonClicked = { returnToMenu() }
    }

    /**
     * Attempts to join a game lobby by ID
     *
     * @param lobbyID String representing the lobby to join
     * @return Boolean indicating success or failure
     */
    fun joinGame(lobbyId: String) {
        val success = Lobby.joinLobby(lobbyId)

        if (success) {
            view.showJoinSuccess()
        } else {
            view.showJoinError("Error message should be put here")
        }
    }

    fun onJoinButtonPressed(lobbyId: String) {
        joinGame(lobbyId)
    }

    /**
     * Return to the menu scene using the presenter manager??
     */
    fun returnToMenu() {
        println("JoinGamePresenter: Returning to menu")
        ScenePresenterStateManage.pop()
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
}
