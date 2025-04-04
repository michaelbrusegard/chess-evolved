package io.github.chessevolved.presenters

import SettingsView
import io.github.chessevolved.PresenterManager
import io.github.chessevolved.singletons.GameSettings

class SettingsPresenter(
    private val settingsView: SettingsView,
) : IPresenter {
    init {
        settingsView.init()
        settingsView.onApply = { fowSetting, sizeSetting ->
            onApplyPressed(fowSetting, sizeSetting)
        }
    }

    private val gameSettings = GameSettings

    /**
     * Applies the chosen game settings and returns to lobby
     *
     * @param fowSetting Boolean for Fog of War
     * @param sizeSetting Int for size of chessboard
     */
    private fun onApplyPressed(
        fowSetting: Boolean,
        sizeSetting: Int,
    ) {
        // TODO: Consider if game settings should be applied manually or automatically
        gameSettings.setFOW(fowSetting)

        // TODO: validate max/min boardsize here?
        gameSettings.setBoardSize(sizeSetting)

        returnToLobby()
    }

    /**
     *  Switch to LobbyPresenter
     */
    private fun returnToLobby() {
        PresenterManager.pop()
    }

    /**
     * Retrieves the current game settings
     *
     * @return Current settings as a Map
     */
    fun getCurrentSettings(): Map<String, Any> =
        mapOf(
            "FogOfWar" to gameSettings.isFOWEnabled(),
            "BoardSize" to gameSettings.getBoardSize(),
        )

    override fun render() {
        settingsView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        settingsView.resize(width, height)
    }

    override fun dispose() {
        settingsView.dispose()
    }

    override fun setInputProcessor() {
        settingsView.setInputProcessor()
    }
}
