package io.github.chessevolved.systems

class LobbySystem {
    fun joinLobby(lobbyId: String): Boolean {
        // Network code would eventually go here
        // For now, just validate the ID format
        return lobbyId.isNotEmpty()
    }
}
