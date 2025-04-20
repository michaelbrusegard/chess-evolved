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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object SupabaseGameHandler {
    private val supabase = getSupabaseClient()
    private const val SUPABASE_GAME_TABLE_NAME = "games"
    var sendingGameState = false

    suspend fun joinGame(
        lobbyCode: String,
        onEventListener: (updatedGame: GameDto) -> Unit,
    ) {
        if (!checkIfGameExists(lobbyCode)) {
            throw Exception("Game does not exist.")
        }
        addGameListener(lobbyCode, onEventListener)
    }

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

        if (response[0].playerDisconnected) {
            try {
                supabase.from(SUPABASE_GAME_TABLE_NAME).delete {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }
            } catch (e: PostgrestRestException) {
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
                throw e
            }
        }

        SupabaseChannelManager.unsubscribeFromChannel("game_$lobbyCode")
    }

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
                        set("want_rematch", value = !response[0].wantRematch)
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
                }.launchIn(coroutineScope)

            channel.subscribe()
        } catch (e: IllegalStateException) {
            throw e
        }
    }

    suspend fun updateGameState(
        lobbyCode: String,
        pieces: List<PieceDto>,
        boardSquares: List<BoardSquareDto>,
        turn: PlayerColor,
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
                },
            ) {
                filter {
                    eq("lobby_code", lobbyCode)
                }
            }
    }

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
