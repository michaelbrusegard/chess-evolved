package io.github.chessevolved.presenters

import io.github.chessevolved.systems.LobbySystem
import io.github.chessevolved.views.JoinGameView

class JoinGamePresenter(
    private val view: JoinGameView,
    private val lobbySystem: LobbySystem = LobbySystem(),
) : IPresenter {
    /**
     * Attempts to join a game lobby by ID
     *
     * @param lobbyID String representing the lobby to join
     * @return Boolean indicating success or failure
     */
    fun joinGame(lobbyID: String): Boolean = lobbySystem.joinLobby(lobbyID)

    fun onJoinButtonPressed() {
        val lobbyId = view.getLobbyId()
        joinGame(lobbyId)
    }

    /**
     * Return to the menu scene using the presenter manager??
     */
    fun returnToMenu() {
        view.closeView()
        // TODO: idk but this should return here to let another presenter handle it
        // presenterManager.popToPresenter(MenuPresenter::class)
    }

    override fun render() {
        view.displayLobbyInput()
    }
}
