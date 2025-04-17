package io.github.chessevolved.singletons

import io.github.chessevolved.singletons.supabase.SupabaseGameHandler
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import kotlin.reflect.KFunction1

object Game {
    private var inGame: Boolean = false
    private var subscribers = mutableMapOf<String, KFunction1<SupabaseGameHandler.Game, Unit>>()
    private var hasAskedForRematch = false

    suspend fun joinGame(gameId: String) {
        try {
            SupabaseGameHandler.joinGame(gameId, ::onGameRowUpdate)
            this.inGame = true
        } catch (e: Exception) {
            throw Exception("Problem with joining game: " + e.message)
        }
    }

    suspend fun leaveGame() {
        if (!isInGame()) {
            throw IllegalStateException("Can't leave game if not in a game.")
        }
        try {
            hasAskedForRematch = false
            SupabaseGameHandler.leaveGame(Lobby.getLobbyId()!!)
            this.inGame = false
        } catch (e: Exception) {
            throw Exception("Problem with leaving game: " + e.message)
        }
    }

    suspend fun askForRematch() {
        if (!isInGame() && !hasAskedForRematch) {
            throw IllegalStateException("Can't ask for rematch if not in a game or have already asked for rematch!")
        }
        try {
            hasAskedForRematch = true
            SupabaseLobbyHandler.setupRematchLobby(Lobby.getLobbyId()!!)
            SupabaseGameHandler.requestRematch(Lobby.getLobbyId()!!)
        } catch (e: Exception) {
            throw Exception("Problem with asking for rematch: " + e.message)
        }
    }

    fun getWantsRematch(): Boolean = hasAskedForRematch

    fun getGameId(): String? = Lobby.getLobbyId()

    fun isInGame(): Boolean = inGame

    private fun onGameRowUpdate(game: SupabaseGameHandler.Game) {
        subscribers.forEach {
            it.value.invoke(game)
        }
    }

    fun subscribeToGameUpdates(
        subscriberName: String,
        onEventListener: KFunction1<SupabaseGameHandler.Game, Unit>,
    ) {
        subscribers.put(subscriberName, onEventListener)
    }

    fun unsubscribeFromGameUpdates(subscriberName: String) {
        if (!subscribers.containsKey(subscriberName)) {
            return
        }

        subscribers.remove(subscriberName)
    }
}
