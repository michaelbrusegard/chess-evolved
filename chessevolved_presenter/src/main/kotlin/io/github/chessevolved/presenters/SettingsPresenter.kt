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
        GameSettings.setFOW(fowSetting)
        GameSettings.setBoardSize(sizeSetting)
        navigator.goBack()
    }

    private fun getCurrentSettings(): Map<String, Any> =
        mapOf(
            "FogOfWar" to GameSettings.isFOWEnabled(),
            "BoardSize" to GameSettings.getBoardSize(),
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
