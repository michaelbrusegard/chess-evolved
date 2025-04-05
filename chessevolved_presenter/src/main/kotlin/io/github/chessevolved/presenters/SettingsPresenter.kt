package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.Navigator
import io.github.chessevolved.singletons.GameSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.github.chessevolved.views.SettingsView

class SettingsPresenter(
    private val settingsView: SettingsView
    private val navigator: Navigator,
) : IPresenter {
    init {
        settingsView.setCurrentSettings(GameSettings.getGameSettings())
        settingsView.init()
        settingsView.onApplyClicked = { fowSetting, sizeSetting ->
            onApplyPressed(fowSetting, sizeSetting)
        }
        settingsView.onCancelClicked = { navigator.goBack() }

        loadCurrentSettingsIntoView()
    }

    private fun loadCurrentSettingsIntoView() {
        val currentSettings = getCurrentSettings()
        settingsView.setInitialValues(
            currentSettings["FogOfWar"] as? Boolean ?: false,
            currentSettings["BoardSize"] as? Int ?: 8,
        )
    }

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
        

    override fun render(sb: SpriteBatch) {
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
