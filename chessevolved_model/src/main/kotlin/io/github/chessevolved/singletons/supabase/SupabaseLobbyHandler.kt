package io.github.chessevolved.singletons.supabase

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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.KSuspendFunction1

object SupabaseLobbyHandler {
    /**
     * Length of lobby-codes
     */
    private const val LOBBY_CODE_LENGTH = 5

    /**
     * Supabase client used to query supabase
     */
    private val supabase = getSupabaseClient()

    /**
     * Supabase lobby-table name
     */
    private val SUPABASE_LOBBY_TABLE_NAME = "lobbies"

    /**
     * Type used for lobbies saved in database.
     */
    @Serializable
    data class Lobby(
        val id: Int,
        val created_at: String,
        val lobby_code: String,
        val second_player: Boolean,
        val game_started: Boolean,
        // TODO: Turn this into a settings-type array when implemented
        val settings: Array<String>,
    )

    // Taken from https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin

    /**
     * Generates a random string containing capital letters and numbers.
     * @param length integer to set the length of the string
     * @return the generated string
     */
    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    // End of taken from

    /**
     * Function to create a lobby on supabase.
     * @return string containing the lobby-code of the lobby created.
     * @throws PostgrestRestException if creating a lobby fails three times.
     */
    suspend fun createLobby(onEventListener: KSuspendFunction1<Lobby, Unit>): String {
        var lobbyCode = getRandomString(LOBBY_CODE_LENGTH)

        for (attempts in 1..3) {
            try {
                supabase.from(SUPABASE_LOBBY_TABLE_NAME).insert(mapOf("lobby_code" to lobbyCode))
                addLobbyListener(lobbyCode, onEventListener)
                return lobbyCode
            } catch (e: PostgrestRestException) {
                if (attempts == 3) {
                    throw e
                }
                lobbyCode = getRandomString(LOBBY_CODE_LENGTH)
            }
        }
        return "" // Code should never reach this, but interpreter doesn't understand that.
    }

    /**
     * Joins a lobby corresponding to the lobbyCode provided.
     * @param lobbyCode corresponding to the lobby the player wants to join
     * @throws Error if a lobby does not exist or a lobby is full
     * @throws IllegalStateException if trying to join a lobby you have already joined.
     */
    suspend fun joinLobby(
        lobbyCode: String,
        onEventListener: KSuspendFunction1<Lobby, Unit>,
    ) {
        val response =
            supabase
                .from(SUPABASE_LOBBY_TABLE_NAME)
                .select {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }.decodeList<Lobby>()

        if (response.isEmpty()) {
            throw Exception("Lobby does not exist.")
        }
        if (response[0].second_player) {
            throw Exception("Lobby is full!")
        }
        addLobbyListener(lobbyCode, onEventListener) // Throws illegalStateException upon already joined lobby
        try {
            supabase
                .from(SUPABASE_LOBBY_TABLE_NAME)
                .update(
                    {
                        set("second_player", value = true)
                    },
                ) {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }
        } catch (e: PostgrestRestException) {
            // If lobby row is deleted right after checking if the lobby exists, we might get an exception here. Throwing error.
            throw e
        }
    }

    /**
     * Method to leave a lobby, marking the row in supabase as not having a second player anymore,
     * or straight up deleting the row if the second player column is already false.
     * Also unsubscribes from the channel that listens to row-level-changes.
     * @param lobbyCode of the lobby to leave
     * @throws Exception if the lobby does not exist
     * @throws PostgrestRestException if trying to leave a lobby that still has an ongoing game
     */
    suspend fun leaveLobby(lobbyCode: String) {
        val response =
            supabase
                .from(SUPABASE_LOBBY_TABLE_NAME)
                .select {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }.decodeList<Lobby>()

        if (response.isEmpty()) {
            throw Exception("Lobby does not exist.")
        }

        if (!response[0].second_player) {
            try {
                supabase.from(SUPABASE_LOBBY_TABLE_NAME).delete {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }
            } catch (e: PostgrestRestException) {
                // Error trying to delete a lobby
                throw e
            }
        } else {
            try {
                supabase
                    .from(SUPABASE_LOBBY_TABLE_NAME)
                    .update(
                        {
                            set("second_player", value = false)
                        },
                    ) {
                        filter {
                            eq("lobby_code", lobbyCode)
                        }
                    }
            } catch (e: PostgrestRestException) {
                // If lobby row is deleted right after checking if the lobby exists, we might get an exception here.
                throw e
            }
        }

        SupabaseChannelManager.unsubscribeFromChannel("lobby_$lobbyCode")
    }

    /**
     * Method that subscribes to row-level-updates on the lobby created/joined
     * @param lobbyCode corresponding to the lobby to subscribe to
     * @param onEventListener corresponding to the method to be called upon row changes
     * @throws IllegalStateException if trying to subscribe to a channel that has already been subscribed to
     */
    private suspend fun addLobbyListener(
        lobbyCode: String,
        onEventListener: KSuspendFunction1<Lobby, Unit>,
    ) {
        val channel = SupabaseChannelManager.getOrCreateChannel("lobby_$lobbyCode")
        try {
            val changeFlow =
                channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                    table = SUPABASE_LOBBY_TABLE_NAME
                    filter("lobby_code", FilterOperator.EQ, lobbyCode)
                }

            val coroutineScope = CoroutineScope(Dispatchers.IO)

            changeFlow
                .onEach {
                    val updatedRecord = it.record
                    val lobby = Json.decodeFromString<Lobby>(updatedRecord.toString())

                    onEventListener(lobby)
                }.launchIn(coroutineScope) // launch a new coroutine to collect the flow

            channel.subscribe()
        } catch (e: IllegalStateException) {
            // When a player tries to join a lobby they have already joined an error will be thrown
            throw e
        }
    }

    /**
     * Class used to create a new row in game table.
     */
    @Serializable
    private class InsertGame(
        val lobby_code: String,
        val settings: Array<String>,
    )

    /**
     * Method to set the "game_started"-column for the lobby table to true in supabase.
     * Also creates a row in the game table on supabase.
     * @param lobbyCode which is the code of the lobby to start the game for.
     * @param gameSettings which are the game-settings to use for this game.
     */
    suspend fun startGame(
        lobbyCode: String,
        gameSettings: Array<String>,
    ) {
        try {
            supabase
                .from("games")
                .insert(
                    InsertGame(lobby_code = lobbyCode, settings = gameSettings),
                )

            supabase
                .from("lobbies")
                .update(
                    {
                        set("game_started", value = true)
                    },
                ) {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }
        } catch (e: PostgrestRestException) {
            // Error when trying to start a game for a lobby that does not exist. OR when both players try to start the lobby at the same time.
            throw e
        }
    }
}
