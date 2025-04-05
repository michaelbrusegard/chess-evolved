package io.github.chessevolved.singletons

import com.badlogic.gdx.utils.Array
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler.Lobby
import kotlin.reflect.KFunction1

object Lobby {
    private var lobbyId: String? = null
    private var subscribers = Array<KFunction1<Lobby, Unit>>()

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

    suspend fun createLobby(): Boolean {
        try {
            val lobbyId = SupabaseLobbyHandler.createLobby(::onLobbyRowUpdate)
            this.lobbyId = lobbyId
            return true
        } catch (e: Exception) {
            // TODO: Elevate exception with appropriate message.
            return false
        }
    }

    suspend fun setLobbySettings() {
        if (lobbyId == null) {
            throw IllegalStateException("Can't update game settings when not in a lobby!")
        }
        SupabaseLobbyHandler.updateLobbySettings(lobbyId!!, GameSettings.getGameSettings())
    }

    suspend fun startGame() {
        if (lobbyId == null) {
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
            it.invoke(lobby)
        }
    }

    fun subscribeToLobbyUpdates(onEventListener: KFunction1<Lobby, Unit>) {
        subscribers.add(onEventListener)
    }

    fun unsubscribeFromLobbyUpdates() {
        // TODO: Might have to change up implementation of subscribing to make it possible to find which function to remove from the subscribed-arraylist.
        // Maybe switch out the array-list with a HashMap, and pass class-name to subscribe-function, so you can unsubscribe by passing class-name.
    }
}
