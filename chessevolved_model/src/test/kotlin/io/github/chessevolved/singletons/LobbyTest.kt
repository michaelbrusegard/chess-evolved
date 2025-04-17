package io.github.chessevolved.singletons

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class LobbyTest {

    /**
     * Attempt to join a lobby that does not exists
     */
    suspend fun joinLobby() {
        Lobby.joinLobby("")
    }

    // Required to test suspend functions
    @Test
    fun testJoinLobby() = runTest {
        try {
            val lobbyTest = joinLobby()
        } catch (e : Exception) {
            println(e.message)
        }
    }

    @Test
    fun createLobby() {
    }

    @Test
    fun leaveLobby() {
    }

    @Test
    fun setLobbySettings() {
    }

    @Test
    fun getLobby() {
    }

    @Test
    fun startGame() {
    }

    @Test
    fun isInLobby() {
    }

    @Test
    fun getLobbyId() {
    }

    @Test
    fun subscribeToLobbyUpdates() {
    }

    @Test
    fun unsubscribeFromLobbyUpdates() {
    }
}
