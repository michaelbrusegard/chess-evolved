package io.github.chessevolved.presenters

import SettingsView
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.PresenterManager
import io.github.chessevolved.singletons.GameSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsPresenter(
    private val settingsView: SettingsView
) : IPresenter {
    init {
        settingsView.setCurrentSettings(GameSettings.getGameSettings())
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
        gameSettings.setFOW(fowSetting)
        gameSettings.setBoardSize(sizeSetting)

        CoroutineScope(Dispatchers.IO).launch {
            Lobby.setLobbySettings()
        }

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
    fun getCurrentSettings(): Map<String, String> {
        return GameSettings.getGameSettings()
    }
        

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
