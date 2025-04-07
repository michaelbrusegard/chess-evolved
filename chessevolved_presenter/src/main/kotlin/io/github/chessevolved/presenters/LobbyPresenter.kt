package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.views.LobbyView

class LobbyPresenter(
    private val lobbyView: LobbyView,
    private val navigator: Navigator,
) : IPresenter {
    init {
        lobbyView.onLeaveButtonClicked = { returnToMenu() }
        lobbyView.onStartGameButtonClicked = { startGame() }
        lobbyView.onOpenSettingsButtonClicked = { enterSettings() }
        lobbyView.init()
    }

    /**
     * Navigate to the Game screen.
     */
    fun startGame() {
        navigator.navigateToGame()
    }

    fun playerJoinedLeftLobby(playerJoined: Boolean) {
        lobbyView.setSecondPlayerConnected(playerJoined)
    }

    private fun enterSettings() {
        navigator.navigateToSettings()
    }

    private fun returnToMenu() {
        navigator.goBack()
    }

    override fun update(dt: Float) {
        // TODO: Add lobby update logic (e.g., check connection status)
    }

    override fun render(sb: SpriteBatch) {
        lobbyView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        lobbyView.resize(width, height)
    }

    override fun dispose() {
        lobbyView.dispose()
    }

    override fun setInputProcessor() {
        lobbyView.setInputProcessor()
    }
}
