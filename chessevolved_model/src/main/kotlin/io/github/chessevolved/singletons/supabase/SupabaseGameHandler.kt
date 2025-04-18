package io.github.chessevolved.singletons.supabase

import io.github.chessevolved.dtos.BoardSquareDto
import io.github.chessevolved.dtos.GameDto
import io.github.chessevolved.dtos.PieceDto
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.singletons.supabase.SupabaseClient.getSupabaseClient
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

object SupabaseGameHandler {
    /**
     * Supabase client used to query supabase
     */
    private val supabase = getSupabaseClient()

    /**
     * Name of the table containing the game rows
     */
    private val SUPABASE_GAME_TABLE_NAME = "games"

    /**
     * Method that checks if a game-row corresponding to a lobbyCode exists, and subscribes
     * to updates on it.
     * @param lobbyCode identifying the game
     * @param onEventListener being the method to be called whenever an update happens
     * @throws Error if the game-row does not exist
     * @throws IllegalStateException if trying to join a game you have already joined
     */
    suspend fun joinGame(
        lobbyCode: String,
        onEventListener: (updatedGame: GameDto) -> Unit,
    ) {
        if (!checkIfGameExists(lobbyCode)) {
            throw Exception("Game does not exist.")
        }
        addGameListener(lobbyCode, onEventListener)
    }

    /**
     * Method that unsubscribes from row-level game updates, and sets player_disconnected to true in the game-row.
     * Cleans up the row if the other player has already left.
     * @param lobbyCode identifying the game.
     * @throws IllegalArgumentException if the game doesn't exist.
     * @throws PostgrestRestException if there are errors within supabase.
     */
    suspend fun leaveGame(lobbyCode: String) {
        val response =
            supabase
                .from(SUPABASE_GAME_TABLE_NAME)
                .select {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }.decodeList<GameDto>()

        if (response.isEmpty()) {
            throw IllegalArgumentException("Game does not exist.")
        }

        if (response[0].player_disconnected) {
            try {
                supabase.from(SUPABASE_GAME_TABLE_NAME).delete {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }
            } catch (e: PostgrestRestException) {
                // Error trying to delete a game
                throw e
            }
        } else {
            try {
                supabase
                    .from(SUPABASE_GAME_TABLE_NAME)
                    .update(
                        {
                            set("player_disconnected", value = true)
                        },
                    ) {
                        filter {
                            eq("lobby_code", lobbyCode)
                        }
                    }
            } catch (e: PostgrestRestException) {
                // Error with supabase when setting disconnected to true on game row.
                throw e
            }
        }

        SupabaseChannelManager.unsubscribeFromChannel("game_$lobbyCode")
    }

    /**
     * Method to set want_rematch in game-row to true.
     * @param lobbyCode
     */
    suspend fun requestRematch(lobbyCode: String) {
        try {
            val response =
                supabase
                    .from(SUPABASE_GAME_TABLE_NAME)
                    .select {
                        filter {
                            eq("lobby_code", lobbyCode)
                        }
                    }.decodeList<GameDto>()

            if (response.isEmpty()) throw IllegalStateException("Illegal state when requesting rematch. Game does not exist.")

            supabase
                .from(SUPABASE_GAME_TABLE_NAME)
                .update(
                    {
                        // This line ensures that the value of want_rematch is the opposite of what it previously were. This way we get the ack-behaviour.
                        set("want_rematch", value = !response[0].want_rematch)
                    },
                ) {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Method to subscribe to updates on a specific game-table row.
     * @param lobbyCode of the game to subscribe to
     * @param onEventListener as the method to be called on updates
     * @throws IllegalStateException if trying to subscribe to a channel that has already been subscribed to
     */
    private suspend fun addGameListener(
        lobbyCode: String,
        onEventListener: (updatedGame: GameDto) -> Unit,
    ) {
        val channel = SupabaseChannelManager.getOrCreateChannel("game_$lobbyCode")

        try {
            val changeFlow =
                channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                    table = "games"
                    filter("lobby_code", FilterOperator.EQ, lobbyCode)
                }

            val coroutineScope = CoroutineScope(Dispatchers.IO)

            changeFlow
                .onEach {
                    val updatedRecord = it.record

                    val game = Json.decodeFromString<GameDto>(updatedRecord.toString())

                    onEventListener(game)
                }.launchIn(coroutineScope) // launch a new coroutine to collect the flow

            channel.subscribe()
        } catch (e: IllegalStateException) {
            // Error when a player tries to join a game they have already joined
            throw e
        }
    }

    /**
     * Method to update the board-column in a specific game.
     * @param lobbyCode identifying the game.
     * @param pieces representing the placement of pieces
     * @param boardSquares representing the events occurring on specific tiles
     * @param turn representing which player's turn it is
     * @param lastMove representing what the last move was
     * @throws Error if the game does not exist
     */
    suspend fun updateGameState(
        lobbyCode: String,
        pieces: PieceDto,
        boardSquares: BoardSquareDto,
        turn: PlayerColor,
        lastMove: String,
    ) {
        if (!checkIfGameExists(lobbyCode)) {
            throw Error("Game does not exist!")
        }

        supabase
            .from("games")
            .update(
                {
                    set("pieces", value = pieces)
                    set("board_squares", value = boardSquares)
                    set("turn", value = turn)
                    set("last_move", value = lastMove)
                },
            ) {
                filter {
                    eq("lobby_code", lobbyCode)
                }
            }
    }

    /**
     * Method that checks if a game-row with a corresponding lobbyCode exists.
     * @param lobbyCode identifying the game.
     * @return Boolean of whether the game exists or not
     */
    private suspend fun checkIfGameExists(lobbyCode: String): Boolean {
        val response =
            supabase
                .from(SUPABASE_GAME_TABLE_NAME)
                .select {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }.decodeList<GameDto>()

        return response.isNotEmpty()
    }
}
