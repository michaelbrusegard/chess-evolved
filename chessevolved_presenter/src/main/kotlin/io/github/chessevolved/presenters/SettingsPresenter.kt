package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.singletons.GameSettings
import io.github.chessevolved.views.SettingsView

class SettingsPresenter(
    private val settingsView: SettingsView,
    private val navigator: Navigator,
) : IPresenter {
    init {
        settingsView.init()
        settingsView.onApply = { fowSetting, sizeSetting ->
            onApplyPressed(fowSetting, sizeSetting)
        }
        settingsView.onCancel = { returnToLobby() }

        val currentSettings = getCurrentSettings()
        settingsView.setInitialValues(
            currentSettings["FogOfWar"] as? Boolean ?: false,
            currentSettings["BoardSize"] as? Int ?: 8,
        )
    }

    private val gameSettings = GameSettings

    private fun onApplyPressed(
        fowSetting: Boolean,
        sizeSetting: Int,
    ) {
        // TODO: Add validation for settings if needed
        gameSettings.setFOW(fowSetting)
        gameSettings.setBoardSize(sizeSetting)

        returnToLobby()
    }

    private fun returnToLobby() {
        navigator.goBack()
    }

    /**
     * Retrieves the current game settings.
     *
     * @return Current settings as a Map.
     */
    fun getCurrentSettings(): Map<String, Any> =
        mapOf(
            "FogOfWar" to gameSettings.isFOWEnabled(),
            "BoardSize" to gameSettings.getBoardSize(),
        )

    override fun update(dt: Float) {
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
