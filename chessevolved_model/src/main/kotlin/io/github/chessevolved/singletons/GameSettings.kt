package io.github.chessevolved.singletons

import io.github.chessevolved.shared.SettingsDTO

object GameSettings {
    private var fogOfWar: Boolean = false
    private var boardSize: Int = 8

    /**
     * The current setting of FOW
     *
     * @return Current FOW setting as Boolean
     */
    fun isFOWEnabled(): Boolean = fogOfWar

    /**
     * The current chessboard size
     *
     * @return Size of the chessboard as Int
     */
    fun getBoardSize(): Int = boardSize

    /**
     * Retrieves all settings as a map of strings
     */
    fun getGameSettings(): SettingsDTO {
        val settingsDTO = SettingsDTO(fogOfWar, boardSize)
        return settingsDTO
    }

    /**
     * Takes a map of strings and updates the settings
     */
    fun setGameSettings(settings: SettingsDTO) {
        fogOfWar = settings.fogOfWar
        boardSize = settings.boardSize
    }
}
