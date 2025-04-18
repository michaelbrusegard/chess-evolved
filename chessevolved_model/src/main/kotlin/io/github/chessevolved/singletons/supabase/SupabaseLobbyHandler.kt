package io.github.chessevolved.singletons.supabase

import io.github.chessevolved.dtos.LobbyDto
import io.github.chessevolved.dtos.SettingsDto
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
    suspend fun createLobby(onEventListener: (updatedLobby: LobbyDto) -> Unit): String {
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
        onEventListener: (updatedLobby: LobbyDto) -> Unit,
    ) {
        val response =
            supabase
                .from(SUPABASE_LOBBY_TABLE_NAME)
                .select {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }.decodeList<LobbyDto>()

        if (response.isEmpty()) {
            throw Exception("Lobby does not exist.")
        }
        if (response[0].secondPlayer) {
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
     * Method that joins and subscribes to row-level lobby updates without
     * updating second_player column to true.
     * @param lobbyCode identifying the lobby
     * @param onEventListener as the method to be called upon row-updates
     */
    suspend fun joinLobbyNoUpdateSecondPlayer(
        lobbyCode: String,
        onEventListener: (updatedLobby: LobbyDto) -> Unit,
    ) {
        val response =
            supabase
                .from(SUPABASE_LOBBY_TABLE_NAME)
                .select {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }.decodeList<LobbyDto>()

        if (response.isEmpty()) {
            throw Exception("Lobby does not exist.")
        }
        if (response[0].secondPlayer) {
            throw Exception("Lobby is full!")
        }
        addLobbyListener(lobbyCode, onEventListener) // Throws illegalStateException upon already joined lobby
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
                }.decodeList<LobbyDto>()

        if (response.isEmpty()) {
            throw Exception("Lobby does not exist.")
        }

        if (!response[0].secondPlayer) {
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
     * Method to leave a lobby without updating the row. Useful for when you want to unsubscribe from row updates.
     * Typically used in rematches where a lobby will be reused.
     * @param lobbyCode identifying the lobby
     * @throws Exception if trying to leave nonexistent channel
     */
    suspend fun leaveLobbyNoUpdateSecondPlayer(lobbyCode: String) {
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
        onEventListener: (updatedLobby: LobbyDto) -> Unit,
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
                    val lobby = Json.decodeFromString<LobbyDto>(updatedRecord.toString())

                    onEventListener(lobby)
                }.launchIn(coroutineScope) // launch a new coroutine to collect the flow

            channel.subscribe()
        } catch (e: IllegalStateException) {
            // When a player tries to join a lobby they have already joined an error will be thrown
            throw e
        }
    }

    /**
     * Method to set the "game_started"-column for the lobby table to true in supabase.
     * Also creates a row in the game table on supabase.
     * @param lobbyCode which is the code of the lobby to start the game for.
     */
    suspend fun startGame(lobbyCode: String) {
        try {
            supabase
                .from("games")
                .insert(
                    mapOf("lobby_code" to lobbyCode),
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

    /**
     * Method to update the settings set in a lobby.
     * @param lobbyCode the code of the lobby to update settings for
     * @param gameSettings the settings to set for the lobby
     * @throws PostgrestRestException if a supabase-error were to happen
     */
    suspend fun updateLobbySettings(
        lobbyCode: String,
        gameSettings: SettingsDto,
    ) {
        try {
            val settingsMap =
                mapOf(
                    "boardSize" to gameSettings.boardSize.toString(),
                    "fogOfWar" to gameSettings.fogOfWar.toString(),
                )

            supabase
                .from(SUPABASE_LOBBY_TABLE_NAME)
                .update(
                    {
                        set("settings", value = settingsMap)
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

    /**
     * Method to get the row for a specific lobby
     * @param lobbyCode that is the code of the lobby to retrieve information about
     * @return Lobby-object representing the data.
     * @throws IllegalArgumentException if the lobby does not exist.
     */
    suspend fun getLobbyRow(lobbyCode: String): LobbyDto {
        val response =
            supabase
                .from(SUPABASE_LOBBY_TABLE_NAME)
                .select {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }.decodeList<LobbyDto>()

        if (response.isEmpty()) {
            throw IllegalArgumentException("Lobby does not exist.")
        }
        return response[0]
    }

    suspend fun setupRematchLobby(lobbyCode: String) {
        try {
            supabase
                .from("lobbies")
                .update(
                    {
                        set("game_started", value = false)
                        set("second_player", value = false)
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
}
