package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.views.LobbyView

class LobbyPresenter(
    private val lobbyView: LobbyView,
    private val navigator: Navigator,
) : IPresenter {
    init {
        lobbyView.onLeaveButtonClicked = { navigator.goBack() }
        lobbyView.onStartGameButtonClicked = { navigator.navigateToGame() }
        lobbyView.onOpenSettingsButtonClicked = { navigator.navigateToSettings() }
        lobbyView.init()
    }

    fun playerJoinedLeftLobby(playerJoined: Boolean) {
        lobbyView.setSecondPlayerConnected(playerJoined)
    }

    override fun update(dt: Float) {
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
