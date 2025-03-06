@file:JvmName("Lwjgl3Launcher")

package io.github.chessevolved.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import io.github.chess2.lwjgl3.StartupHelper
import io.github.chessevolved.ChessEvolvedGame

/** Launches the desktop (LWJGL3) application. */
fun main() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(ChessEvolvedGame(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Chess2")
        setWindowedMode(800, 800)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
