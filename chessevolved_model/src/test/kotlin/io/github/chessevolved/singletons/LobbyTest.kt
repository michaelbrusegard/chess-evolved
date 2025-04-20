package io.github.chessevolved.singletons

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertFails

class LobbyTest {
    suspend fun joinLobby() {
        Lobby.joinLobby("E6U5Y5GTRFEZE45")
    }

    /**
     * Attempt to join a lobby that does not exists
     */
    @Test
    fun testJoinLobby() =
        runTest { // Required to test suspend functions
            assertFails({ joinLobby() })
        }

    suspend fun joinRematchLobbyAsHost() {
        Lobby.joinRematchLobbyAsHost()
    }

    /**
     * Attempt to join a rematch lobby as a host that does not exists
     */
    @Test
    fun testJoinRematchLobbyAsHost() =
        runTest { // Required to test suspend functions
            assertFails({ joinRematchLobbyAsHost() })
        }

    suspend fun joinRematchLobbyNonHost() {
        Lobby.joinRematchLobbyNonHost()
    }

    /**
     * Attempt to join a rematch lobby as a non-host that does not exists
     */
    @Test
    fun testJoinRematchLobbyNonHost() =
        runTest { // Required to test suspend functions
            assertFails({ joinRematchLobbyNonHost() })
        }

    suspend fun leaveLobby() {
        Lobby.leaveLobby()
    }

    /**
     * Attempt to leave a lobby despite not being in a lobby
     */
    @Test
    fun testLeaveLobby() =
        runTest { // Required to test suspend functions
            assertFails({ leaveLobby() })
        }

    suspend fun leaveLobbyWithoutUpdating() {
        Lobby.leaveLobbyWithoutUpdating()
    }

    /**
     * Attempt to leave a lobby as a second player despite not being in a lobby
     */
    @Test
    fun testLeaveLobbyWithoutUpdating() =
        runTest { // Required to test suspend functions
            assertFails({ leaveLobbyWithoutUpdating() })
        }

    suspend fun setLobbySettings() {
        Lobby.setLobbySettings()
    }

    /**
     * Attempt to change lobby settings despite not being in a lobby
     */
    @Test
    fun testSetLobbySettings() =
        runTest { // Required to test suspend functions
            assertThrows<IllegalStateException>({ setLobbySettings() })
        }
}
