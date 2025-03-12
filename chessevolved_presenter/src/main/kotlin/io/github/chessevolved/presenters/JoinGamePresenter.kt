package io.github.chessevolved.presenters

import io.github.chessevolved.systems.LobbySystem
import io.github.chessevolved.views.IView

class JoinGamePresenter(
    joinGameView: IView? = null,
    private val lobbySystem: LobbySystem = LobbySystem(),
) : IPresenter {
    val view: IView? = joinGameView

    /**
     * Attempts to join a game lobby by ID
     *
     * @param lobbyID String representing the lobby to join
     * @return Boolean indicating success or failure
     */
    fun joinGame(lobbyID: String): Boolean = lobbySystem.joinLobby(lobbyID)

    /**
     * Return to the menu scene using the presenter stack
     */
    fun returnToMenu() {
        // TODO: idk but this should return here to let another presenter handle it
        // presenterManager.popToPresenter(MenuPresenter::class)
    }

    /**
     * Get the current state of the join game view
     *
     * @return Map containing view state information
     */
    fun getJoinGameState(): Map<String, Any> =
        mapOf(
            "title" to "Join Game",
            "placeholderText" to "Enter Lobby ID",
            // Add more state information as needed by the view
        )

    override fun render() {
        // Required by IPresenter
        // The view might need to show a lobby ID input field and buttons
        // This will be implemented when the view is ready
    }
}
