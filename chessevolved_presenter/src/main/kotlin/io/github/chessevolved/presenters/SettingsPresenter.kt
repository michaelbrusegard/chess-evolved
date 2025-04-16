package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.shared.SettingsDTO
import io.github.chessevolved.singletons.GameSettings
import io.github.chessevolved.singletons.Lobby.setLobbySettings
import io.github.chessevolved.views.SettingsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsPresenter(
    private val settingsView: SettingsView,
    private val navigator: Navigator,
) : IPresenter {
    init {
        settingsView.init()
        settingsView.setExistingSettings(
            SettingsDTO(
                fogOfWar = GameSettings.isFOWEnabled(),
                boardSize = GameSettings.getBoardSize(),
            ),
        )
        settingsView.onApplyClicked = { settingsDTO ->
            onApplyPressed(settingsDTO)
        }
        settingsView.onCancelClicked = { navigator.goBack() }

        loadCurrentSettingsIntoView()
    }

    private fun loadCurrentSettingsIntoView() {
        val currentSettings =
            SettingsDTO(
                fogOfWar = GameSettings.isFOWEnabled(),
                boardSize = GameSettings.getBoardSize(),
            )
        settingsView.setInitialValues(currentSettings)
    }

    private fun onApplyPressed(settings: SettingsDTO) {
        // Sets settings locally, must be done because Lobby uses local settings to update supabase
        GameSettings.setGameSettings(settings)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                setLobbySettings()
            } catch (e: Exception) {
                error("Error fetching lobby settings from supabase: " + e.message)
            }
        }

        navigator.goBack()
    }

    private fun getCurrentSettings(): Map<String, Any> =
        mapOf(
            "FogOfWar" to GameSettings.isFOWEnabled(),
            "BoardSize" to GameSettings.getBoardSize(),
        )

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
