package io.github.chessevolved.singletons

object Lobby {
    var lobbyId: String? = null
    // This needs to do more things but I dont remember all the code from the supabase setup

    fun joinLobby(lobbyId: String): Boolean {
        // Network code would eventually go here
        // For now, just validate the ID format
        println("Lobby: Joining lobby with ID: $lobbyId")
        return true
    }

    fun createLobby(): Boolean {
        // Will create a new lobby and store id in lobby state
        return true
    }

    fun isInLobby(): Boolean {
        // if the player is in a lobby
        return true
    }
}
