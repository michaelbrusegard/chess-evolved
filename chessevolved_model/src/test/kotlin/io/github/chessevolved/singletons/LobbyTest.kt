package io.github.chessevolved.singletons

import kotlinx.coroutines.test.runTest

import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class LobbyTest {

    //TODO: update current tests, write test for the rest of the methods (Unit test)

    /**
     * Attempt to join a lobby that does not exists
     */
    suspend fun joinLobby() {
        Lobby.joinLobby("")
    }
    // Required to test suspend functions
    @Test
    fun testJoinLobby() = runTest {
        assertFails({ joinLobby() })
    }

    suspend fun createLobby() {
        Lobby.createLobby()
    }

    @Test
    fun testCreateLobby() = runTest {
        assertFails({ createLobby() })
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
