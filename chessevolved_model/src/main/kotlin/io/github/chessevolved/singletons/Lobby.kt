package io.github.chessevolved.singletons

import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler.Lobby
import kotlin.reflect.KFunction1

object Lobby {
    private var lobbyId: String? = null
    private var subscribers = mutableMapOf<String, KFunction1<Lobby, Unit>>()

    /**
     * Method to join a lobby.
     * @param lobbyId that is the lobbyId of the lobby to join
     * @throws Exception if something goes wrong.
     */
    suspend fun joinLobby(lobbyId: String) {
        println("Lobby: Joining lobby with ID: $lobbyId...")
        try {
            SupabaseLobbyHandler.joinLobby(lobbyId, ::onLobbyRowUpdate)
            this.lobbyId = lobbyId
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun createLobby() {
        try {
            val lobbyId = SupabaseLobbyHandler.createLobby(::onLobbyRowUpdate)
            this.lobbyId = lobbyId
        } catch (e: Exception) {
            throw Exception("Problem when creating lobby! " + e.message)
        }
    }

    suspend fun leaveLobby() {
        if (!isInLobby()) {
            throw Exception("Can't leave lobby when not in a lobby!")
        }
        try {
            SupabaseLobbyHandler.leaveLobby(lobbyId!!)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun setLobbySettings() {
        if (!isInLobby()) {
            throw IllegalStateException("Can't update game settings when not in a lobby!")
        }
        SupabaseLobbyHandler.updateLobbySettings(lobbyId!!, GameSettings.getGameSettings())
    }

    suspend fun getLobby(): Lobby {
        if (!isInLobby()) {
            throw IllegalStateException("Can't retrieve lobby if not in a lobby yet.")
        }
        try {
            val lobby = SupabaseLobbyHandler.getLobbyRow(lobbyId!!)
            return lobby
        } catch (e: Exception) {
            throw Exception("Something went wrong trying to fetch lobby: " + e.message)
        }
    }

    suspend fun startGame() {
        if (!isInLobby()) {
            throw IllegalStateException("Can't start game when not in a lobby!")
        }
        try {
            SupabaseLobbyHandler.startGame(lobbyId!!, GameSettings.getGameSettings())
        } catch (e: Exception) {
            // TODO: Elevate exception with appropriate message
        }
    }

    fun isInLobby(): Boolean = !lobbyId.isNullOrEmpty()

    fun getLobbyId(): String? = lobbyId

    private fun onLobbyRowUpdate(lobby: SupabaseLobbyHandler.Lobby) {
        subscribers.forEach {
            it.value.invoke(lobby)
        }
    }

    fun subscribeToLobbyUpdates(
        subscriberName: String,
        onEventListener: KFunction1<Lobby, Unit>,
    ) {
        subscribers.put(subscriberName, onEventListener)
    }

    fun unsubscribeFromLobbyUpdates(subscriberName: String) {
        if (!subscribers.containsKey(subscriberName)) {
            return
        }

        subscribers.remove(subscriberName)
    }
}
