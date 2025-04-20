package io.github.chessevolved.singletons

import io.github.chessevolved.dtos.SettingsDto
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

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
        val settingsDTO: SettingsDto = SettingsDto(false, 8)
        assertEquals(settingsDTO, GameSettings.getGameSettings())
    }

    @Test
    fun setGameSettings() {
        val settingsDTO: SettingsDto = SettingsDto(true, 16)
        GameSettings.setGameSettings(settingsDTO)
        assertEquals(settingsDTO, GameSettings.getGameSettings())
        assertEquals(true, GameSettings.isFOWEnabled())
        assertEquals(16, GameSettings.getBoardSize())
    }
}
