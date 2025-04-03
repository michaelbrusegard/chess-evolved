package io.github.chessevolved.presenters

import SettingsView
import io.github.chessevolved.singletons.GameSettings

class SettingsPresenter(
    private val view: SettingsView,
) : IPresenter {
    init {
        view.init()
        view.onApply = { fowSetting, sizeSetting ->
            onApplyPressed(fowSetting, sizeSetting)
        }
    }

    // TODO: wait for implementation of io.github.chessevolved.ScenePresenterStateManager
    //  UPDATE: just add to stack where necessary, but a setting-button doesnt exist yet
    private val gameSettings = GameSettings
    // val presenterManager = io.github.chessevolved.ScenePresenterStateManager

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
        // TODO: wait for implementation of io.github.chessevolved.ScenePresenterStateManager
        println("SettingsPresenter: Returning to lobby")
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
        view.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        view.resize(width, height)
    }

    override fun dispose() {
        view.dispose()
    }

    override fun setInputProcessor() {
        TODO("Not yet implemented")
    }
}
