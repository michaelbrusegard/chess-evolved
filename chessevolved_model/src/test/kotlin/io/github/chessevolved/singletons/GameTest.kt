package io.github.chessevolved.singletons

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.test.Test
import kotlin.test.assertFails

class GameTest {
    suspend fun joinGame() {
        Game.joinGame("HY76UYTERFRGET")
    }

    /**
     * Attempt to join a game that does not exists
     */
    @Test
    fun testJoinGame() =
        runTest { // Required to test suspend functions
            assertFails({ joinGame() })
        }

    suspend fun leaveGame() {
        Game.leaveGame()
    }

    /**
     * Attempt to leave a game despite not being in a game
     */
    @Test
    fun testLeaveGame() =
        runTest {
            assertFails({ leaveGame() })
        }

    suspend fun askForRematch() {
        Game.askForRematch()
    }

    /**
     * Attempt to ask for a rematch despite not being in a game
     */
    @Test
    fun testAskForRematch() =
        runTest {
            assertFails({ askForRematch() })
        }

    @Test
    fun getWantsRematch() {
        assertFalse(Game.getWantsRematch())
    }

    @Test
    fun isInGame() {
        assertFalse(Game.isInGame())
    }
}
