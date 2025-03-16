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
     * Type used for games saved in database.
     */
    @Serializable
    data class Game(
        val id: Int,
        val updated_at: String,
        val lobby_code: String,
        val last_move: String,
        val turn: String, // TODO: Make this a color enum?
        val board: String, // TODO: Make a board-class that the board-json can be decoded to.
    )

    /**
     * Method to subscribe to updates on a specific game-table row.
     * @param lobbyCode of the game to subscribe to
     * @param onEventListener as the method to be called on updates
     */
    suspend fun addGameListener(
        lobbyCode: String,
        onEventListener: (newGameRow: Game) -> Unit,
    ) {
        // TODO: More error handling
        val channel = SupabaseChannelManager.getOrCreateChannel("game_$lobbyCode")
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
    }

    /**
     * Method to update the board-column in a specific game.
     * @param lobbyCode of the game to update the column for
     */
    suspend fun updateGameBoard(
        lobbyCode: String,
        board: String,
    ) {
        // TODO: More error handling
        supabase
            .from("games")
            .update(
                {
                    set("board", value = board)
                },
            ) {
                filter {
                    eq("lobby_code", lobbyCode)
                }
            }
    }
}
