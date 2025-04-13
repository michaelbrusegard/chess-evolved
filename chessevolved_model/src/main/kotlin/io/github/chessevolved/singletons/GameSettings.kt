package io.github.chessevolved.singletons

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
    fun getGameSettings(): Map<String, String> {
        val settingMap =
            mapOf(
                "boardSize" to boardSize.toString(),
                "fogOfWar" to fogOfWar.toString(),
            )
        return settingMap
    }

    /**
     * Takes a map of strings and updates the settings
     */
    fun setGameSettings(settingsMap: Map<String, String>) {
        for ((setting, value) in settingsMap) {
            when (setting) {
                "fogOfWar" -> fogOfWar = value.toBooleanStrictOrNull() ?: false
                "boardSize" -> boardSize = value.toIntOrNull() ?: 8
            }
        }
        print("settings updated by listner")
    }
}
