package io.github.chessevolved.presenters

import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.views.JoinGameView

class JoinGamePresenter(
    private val view: JoinGameView,
) : IPresenter {
    /**
     * Attempts to join a game lobby by ID
     *
     * @param lobbyID String representing the lobby to join
     * @return Boolean indicating success or failure
     */
    fun joinGame(lobbyId: String): Boolean {
        val success = Lobby.joinLobby(lobbyId)

        if (success) {
            view.showJoinSuccess()
        } else {
            view.showJoinError("Unable to join lobby")
        }

        return success
    }

    fun onJoinButtonPressed() {
        println("JoinGamePresenter: Join button pressed")
        val lobbyId = view.getLobbyId()
        joinGame(lobbyId)
    }

    /**
     * Return to the menu scene using the presenter manager??
     */
    fun returnToMenu() {
        println("JoinGamePresenter: Returning to menu")
        // TODO: idk but this should return here to let another presenter handle it
        // presenterManager.popToPresenter(MenuPresenter::class)
    }

    override fun render() {
        view.displayLobbyInput()
    }
}
