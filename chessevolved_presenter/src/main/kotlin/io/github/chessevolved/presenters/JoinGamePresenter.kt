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
    fun joinGame(lobbyID: String): Boolean {
        println("JoinGamePresenter: Attempting to join lobby with ID: $lobbyID")

        val success = lobbySystem.joinLobby(lobbyID)

        if (success) {
            println("JoinGamePresenter: Successfully joined lobby $lobbyID")
            view.showJoinSuccess()
        } else {
            println("JoinGamePresenter: Failed to join lobby $lobbyID")
            view.showJoinError("Unable to join lobby: $lobbyID")
        }

        return success
    }

    fun onJoinButtonPressed() {
        println("JoinGamePresenter: Join button pressed")
        val lobbyId = view.getLobbyId()
        println("JoinGamePresenter: Retrieved lobby ID from view: '$lobbyId'")
        joinGame(lobbyId)
    }

    /**
     * Return to the menu scene using the presenter manager??
     */
    fun returnToMenu() {
        println("JoinGamePresenter: Returning to menu")
        view.closeView()
        println("JoinGamePresenter: View closed")
        // TODO: idk but this should return here to let another presenter handle it
        // presenterManager.popToPresenter(MenuPresenter::class)
    }

    override fun render() {
        println("JoinGamePresenter: Rendering join game view")
        view.displayLobbyInput()
        println("JoinGamePresenter: Join game view displayed")
    }
}
