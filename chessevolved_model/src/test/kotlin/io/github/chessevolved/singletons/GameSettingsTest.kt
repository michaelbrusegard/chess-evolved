package io.github.chessevolved.singletons

import io.github.chessevolved.shared.SettingsDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GameSettingsTest {
    @Test
    fun isFOWEnabled() {
        assertEquals(false, GameSettings.isFOWEnabled())
    }

    @Test
    fun getBoardSize() {
        assertEquals(8, GameSettings.getBoardSize())
    }

    @Test
    fun getGameSettings() {
        val settingsDTO: SettingsDTO = SettingsDTO(false, 8)
        assertEquals(settingsDTO, GameSettings.getGameSettings())
    }

    @Test
    fun setGameSettings() {
        val settingsDTO: SettingsDTO = SettingsDTO(true, 16)
        GameSettings.setGameSettings(settingsDTO)
        assertEquals(settingsDTO, GameSettings.getGameSettings())
        assertEquals(true, GameSettings.isFOWEnabled())
        assertEquals(16, GameSettings.getBoardSize())
    }
}
