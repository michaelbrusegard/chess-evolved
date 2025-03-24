package io.github.chessevolved.singletons.supabase

import io.github.chessevolved.singletons.supabase.SupabaseClient.getSupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
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

    enum class TurnColor {
        WHITE,
        BLACK,
    }

    /**
     * Type used for games saved in database.
     */
    @Serializable
    data class Game(
        val id: Int,
        val updated_at: String,
        val lobby_code: String,
        val last_move: String?,
        val turn: TurnColor,
        val pieces: Array<String>, // TODO: Change this to be an array of pieces when that is implemented
        val board_squares: Array<String>, // TODO: Change this to be an array of board squares when that is implemented
        val settings: Array<String>, // TODO: Change this into a settings type array when implemented
    )

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
        onEventListener: (newGameRow: Game) -> Unit,
    ) {
        if (!checkIfGameExists(lobbyCode)) {
            throw Exception("Game does not exist.")
        }
        addGameListener(lobbyCode, onEventListener)
    }

    /**
     * Method to subscribe to updates on a specific game-table row.
     * @param lobbyCode of the game to subscribe to
     * @param onEventListener as the method to be called on updates
     * @throws IllegalStateException if trying to subscribe to a channel that has already been subscribed to
     */
    private suspend fun addGameListener(
        lobbyCode: String,
        onEventListener: (newGameRow: Game) -> Unit,
    ) {
        // TODO: More error handling
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

                    val game = Json.decodeFromString<Game>(updatedRecord.toString())

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
     * @param lobbyCode of the game to update the column for
     * @param pieces representing the placement of pieces
     * @param boardSquares representing the events occurring on specific tiles
     * @param turn representing which player's turn it is
     * @param lastMove representing what the last move was
     * @throws Error if the game does not exist
     */
    suspend fun updateGameState(
        lobbyCode: String,
        pieces: Array<String>,
        boardSquares: Array<String>,
        turn: TurnColor,
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
     * @param lobbyCode of the game to look for
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
                }.decodeList<Game>()

        return response.isNotEmpty()
    }
}
