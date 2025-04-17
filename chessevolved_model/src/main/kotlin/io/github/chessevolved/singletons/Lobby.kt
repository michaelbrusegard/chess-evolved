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

    suspend fun joinRematchLobbyAsHost() {
        println("Lobby: Joining rematch-lobby with ID: $lobbyId...")
        if (!isInLobby()) {
            throw Exception("Can't rematch when not in a lobby!")
        }
        try {
            SupabaseLobbyHandler.leaveLobbyNoUpdateSecondPlayer(lobbyId!!)
            SupabaseLobbyHandler.joinLobbyNoUpdateSecondPlayer(lobbyId!!, ::onLobbyRowUpdate)
        } catch (e: Exception) {
            println(e.message)
            error(e)
            // throw e
        }
    }

    suspend fun joinRematchLobbyNonHost() {
        if (!isInLobby()) {
            throw Exception("Can't rematch when not in a lobby!")
        }
        try {
            SupabaseLobbyHandler.leaveLobbyNoUpdateSecondPlayer(lobbyId!!)
            SupabaseLobbyHandler.joinLobby(lobbyId!!, ::onLobbyRowUpdate)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun createLobby() {
        try {
            val lobbyId = SupabaseLobbyHandler.createLobby(::onLobbyRowUpdate)
            this.lobbyId = lobbyId
            println("Lobby: Creating lobby with ID: $lobbyId...")
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
            this.lobbyId = null
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun leaveLobbyWithoutUpdating() {
        if (!isInLobby()) {
            throw Exception("Can't leave lobby when not in a lobby!")
        }
        try {
            SupabaseLobbyHandler.leaveLobbyNoUpdateSecondPlayer(lobbyId!!)
            lobbyId = null
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun setLobbySettings() {
        if (!isInLobby()) {
            throw IllegalStateException("Can't update game settings when not in a lobby!")
        }
        try {
            SupabaseLobbyHandler.updateLobbySettings(lobbyId!!, GameSettings.getGameSettings())
        } catch (e: Exception) {
            throw Exception("Problem updating lobby settings! " + e.message)
        }
    }

    // suspend fun getLobbySettings(): Map<String, String> {
    //     if (!isInLobby()) {
    //         throw IllegalStateException("Can't get game settings when not in a lobby!")
    //     }
    //     val settingsArray = SupabaseLobbyHandler.getLobbyRow(lobbyId!!).settings
    //     val settingsMap = mapOf(
    //         settingsArray
    //     )
    // }

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
            SupabaseLobbyHandler.startGame(lobbyId!!)
        } catch (e: Exception) {
            throw e
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
