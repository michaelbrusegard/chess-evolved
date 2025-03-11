package io.github.chessevolved.supabase

import io.github.chessevolved.supabase.SupabaseClient.getSupabaseClient
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
     * Type used for lobbies saved in database.
     */
    @Serializable
    data class Lobby(
        val id: Int,
        val created_at: String,
        val lobby_code: String,
        val second_player: Boolean,
        val game_started: Boolean
    )

    // Taken from https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin
    /**
     * Generates a random string containing capital letters and numbers.
     * @param length integer to set the length of the string
     * @return the generated string
     */
    private fun getRandomString(length: Int) : String {
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
        println("LAUNCHING LOBBY")
        var lobbyCode = getRandomString(LOBBY_CODE_LENGTH);

        for (attempts in 1..3) {
            try {
                supabase.from("lobbies").insert(mapOf("lobby_code" to lobbyCode))
                addLobbyListener(lobbyCode, onEventListener)
                return lobbyCode
            } catch (e : PostgrestRestException) {
                if (attempts == 3) {
                    throw e
                }
                lobbyCode = getRandomString(LOBBY_CODE_LENGTH)
            }
        }
        return ""; // Code should never reach this, but interpreter doesn't understand that.
    }

    /**
     * Joins a lobby corresponding to the lobbyCode provided.
     * @param lobbyCode corresponding to the lobby the player wants to join
     * @throws Error if a lobby does not exist or a lobby is full
     */
    suspend fun joinLobby(lobbyCode: String, onEventListener: KSuspendFunction1<Lobby, Unit>) {
        // TODO: Change up what type of error is thrown upon full and non-existent lobbies.
        val response = supabase.from("lobbies").select() {
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
        //addLobbyListener(lobbyCode, onEventListener)
        try {
            supabase.from("lobbies")
                .update(
                    {
                        set("second_player", value = true)
                    }
                )
                {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }
        } catch (e : PostgrestRestException) {
            // TODO: If lobby row is deleted right after checking if the lobby exists, we might get an exception here.
        }
    }

    private suspend fun addLobbyListener(lobbyCode: String, onEventListener: KSuspendFunction1<Lobby, Unit>) {
        val channel = SupabaseChannelSubscriber.getOrSubscribeToChannel(lobbyCode)

        val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "lobbies"
            filter("lobby_code", FilterOperator.EQ, lobbyCode)
        }

        val coroutineScope = CoroutineScope(Dispatchers.IO)

        changeFlow.onEach {
            val updatedRecord = it.record
            val lobby = Json.decodeFromString<Lobby>(updatedRecord.toString())

            onEventListener(lobby)
        }.launchIn(coroutineScope) // launch a new coroutine to collect the flow

        channel.subscribe()
    }

    suspend fun startGame(lobbyCode : String) {
        try {
            supabase.from("games")
                .insert(mapOf("lobby_code" to lobbyCode))

            supabase.from("lobbies")
                .update(
                    {
                        set("game_started", value = true)
                    }
                )
                {
                    filter {
                        eq("lobby_code", lobbyCode)
                    }
                }

        } catch (e : PostgrestRestException) {
            // TODO: Handle error when trying to start a game for a lobby that does not exist.
        }
    }
}
