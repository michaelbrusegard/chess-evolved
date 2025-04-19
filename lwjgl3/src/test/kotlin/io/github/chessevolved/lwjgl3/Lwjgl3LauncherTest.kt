package io.github.chessevolved.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.chessevolved.ChessEvolvedGame
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class Lwjgl3LauncherTest {
    @Test
    fun testUI() {
        val config: Lwjgl3ApplicationConfiguration =
            Lwjgl3ApplicationConfiguration().apply {
                setTitle("Chess Evolved")
                setWindowedMode(360, 800)
                setWindowIcon(
                    *(
                        arrayOf(128, 64, 32, 16)
                            .map { "libgdx$it.png" }
                            .toTypedArray()
                    ),
                )
            }
        config.disableAudio(true)
        val testApplication: Lwjgl3Application = Lwjgl3Application(ChessEvolvedGame(), config)
        assertDoesNotThrow({ testApplication.exit() })
    }
}
