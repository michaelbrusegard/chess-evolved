package io.github.chessevolved.singletons

import io.github.chessevolved.dtos.BoardSquareDto
import io.github.chessevolved.dtos.GameDto
import io.github.chessevolved.dtos.PieceDto
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler

object Game {
    private var inGame: Boolean = false
    private var subscribers = mutableMapOf<String, (updatedGame: GameDto) -> Unit>()
    private var hasAskedForRematch = false
    private var currentTurn: PlayerColor? = null

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
            this.currentTurn = null
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

    fun getCurrentTurn(): PlayerColor? = currentTurn

    suspend fun updateGameState(
        lobbyCode: String,
        pieces: List<PieceDto>,
        boardSquares: List<BoardSquareDto>,
    ) {
        val nextTurn =
            if (currentTurn == PlayerColor.WHITE) PlayerColor.BLACK else PlayerColor.WHITE
        try {
            SupabaseGameHandler.updateGameState(lobbyCode, pieces, boardSquares, nextTurn)
            currentTurn = nextTurn
        } catch (e: Exception) {
            throw Exception("Problem updating game state: " + e.message)
        }
    }

    private fun onGameRowUpdate(game: GameDto) {
        subscribers.forEach {
            it.value.invoke(game)
        }
        game.turn.let {
            currentTurn = PlayerColor.valueOf(it.toString())
        }
    }

    fun subscribeToGameUpdates(
        subscriberName: String,
        onEventListener: (updatedGame: GameDto) -> Unit,
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
