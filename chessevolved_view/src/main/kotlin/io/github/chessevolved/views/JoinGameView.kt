package io.github.chessevolved.views

interface JoinGameView {
    fun displayLobbyInput()

    fun showJoinSuccess()

    fun showJoinError(message: String)

    fun getLobbyId(): String

    fun closeView()
}

// This is temporary for now
class JoinGameViewImpl : JoinGameView {
    private var lobbyIdInput: String = ""

    override fun displayLobbyInput() {
        // Setup UI for lobby input
    }

    override fun showJoinSuccess() {
        // Show success state
    }

    override fun showJoinError(message: String) {
        // Show error with message
    }

    override fun getLobbyId(): String = lobbyIdInput

    fun setLobbyId(id: String) {
        lobbyIdInput = id
    }

    override fun closeView() {
        // Handle view closing
    }
}
