package io.github.chessevolved.singletons

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class LobbyTest {

    /**
     * Attempt to join a lobby that does not exists
     */
    suspend fun joinLobby() {
        Lobby.joinLobby("E6U5Y5GTRFEZE45")
    }

    // Required to test suspend functions
    @Test
    fun testJoinLobby() =
        runTest {
            assertFails({ joinLobby() })
        }
}
