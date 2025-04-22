package io.github.chessevolved.singletons

import com.badlogic.ashley.core.Entity
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.dtos.BoardSquareDto
import io.github.chessevolved.dtos.GameDto
import io.github.chessevolved.dtos.PieceDto
import io.github.chessevolved.enums.AbilityType
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler

object Game {
    private var inGame: Boolean = false
    private var subscribers = mutableMapOf<String, (updatedGame: GameDto) -> Unit>()
    private var hasAskedForRematch = false
    private var currentTurn: PlayerColor? = null
    var turnNumber: Int = 0

    private var pieceDTOS: MutableMap<Entity, PieceDto> = mutableMapOf()

    suspend fun joinGame(gameId: String) {
        try {
            SupabaseGameHandler.joinGame(gameId, ::onGameRowUpdate)
            this.inGame = true
            this.currentTurn = PlayerColor.WHITE
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

    suspend fun deleteGame() {
        leaveGame()
        SupabaseGameHandler.deleteGameRow(Lobby.getLobbyId()!!)
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

    fun getPieceDTOS(): MutableMap<Entity, PieceDto> {
        return pieceDTOS
    }

    fun addPieceDTOS(entity: Entity) {
        val pieceDTO =
            PieceDto(
                position = PositionComponent.mapper.get(entity).position,
                previousPosition = PositionComponent.mapper.get(entity).position,
                type = PieceTypeComponent.mapper.get(entity).type,
                color = PlayerColorComponent.mapper.get(entity).color,
                abilityType = AbilityComponent.mapper.get(entity)?.ability,
                abilityCurrentCooldown =
                    if (AbilityComponent.mapper.get(entity) != null) {
                        AbilityComponent.mapper.get(
                            entity,
                        ).currentAbilityCDTime
                    } else {
                        0
                    },
            )

        pieceDTOS[entity] = pieceDTO
    }

    fun changePieceDTOPosition(
        entity: Entity,
        targetPosition: Position,
    ) {
        if (pieceDTOS.containsKey(entity)) {
            pieceDTOS[entity]!!.previousPosition = pieceDTOS[entity]!!.position
            pieceDTOS[entity]!!.position = targetPosition
        }
    }

    fun changePieceDTOAbility(
        entity: Entity,
        abilityType: AbilityType?,
        abilityCurrentCooldown: Int,
    ) {
        if (pieceDTOS.containsKey(entity)) {
            pieceDTOS[entity]!!.abilityType = abilityType
            pieceDTOS[entity]!!.abilityCurrentCooldown = abilityCurrentCooldown
        }
    }

    fun removeEntityFromPieceDTOS(entity: Entity) {
        pieceDTOS.remove(entity)
    }
}
