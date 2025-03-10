package io.github.chessevolved.supabase

import io.github.chessevolved.supabase.SupabaseClient.getSupabaseClient
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.result.PostgrestResult

object SupabaseLobbyHandler {
    /**
     * Length of lobby-codes
     */
    private const val LOBBY_CODE_LENGTH = 5

    /**
     * Supabase client used to query supabase
     */
    private val supabase = getSupabaseClient()

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
    suspend fun createLobby(): String {
        // TODO: Add subscribe to row to see if other players join the lobby.
        println("LAUNCHING LOBBY")
        var lobbyCode = getRandomString(LOBBY_CODE_LENGTH);

        for (attempts in 1..3) {
            try {
                supabase.from("lobbies").insert(mapOf("lobby_code" to lobbyCode))
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
}
