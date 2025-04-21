package io.github.chessevolved.singletons

import io.github.chessevolved.dtos.SettingsDto
import io.github.chessevolved.enums.PlayerColor

object GameSettings {
    private var fogOfWar: Boolean = false
    private var boardSize: Int = 8
    var clientPlayerColor: PlayerColor = PlayerColor.WHITE
    var opponentPlayerColor: PlayerColor = PlayerColor.BLACK
    var isSecondPlayer: Boolean = false

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
    fun getGameSettings(): SettingsDto {
        val settingsDTO = SettingsDto(fogOfWar, boardSize)
        return settingsDTO
    }

    /**
     * Takes a map of strings and updates the settings
     */
    fun setGameSettings(settings: SettingsDto) {
        fogOfWar = settings.fogOfWar
        boardSize = settings.boardSize
    }
}
