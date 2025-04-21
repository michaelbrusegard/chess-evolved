package io.github.chessevolved

import com.badlogic.gdx.assets.AssetManager
import io.github.chessevolved.presenters.EndGamePresenter
import io.github.chessevolved.presenters.GamePresenter
import io.github.chessevolved.presenters.JoinGamePresenter
import io.github.chessevolved.presenters.LobbyPresenter
import io.github.chessevolved.presenters.MenuPresenter
import io.github.chessevolved.presenters.SettingsPresenter
import io.github.chessevolved.views.EndGameView
import io.github.chessevolved.views.JoinGameView
import io.github.chessevolved.views.LobbyView
import io.github.chessevolved.views.MenuView
import io.github.chessevolved.views.SettingsView

class Navigator(
    private val assetManager: AssetManager,
) {
    private fun createMenuPresenter(): MenuPresenter = MenuPresenter(MenuView(), this)

    private fun createJoinGamePresenter(): JoinGamePresenter = JoinGamePresenter(JoinGameView(), this)

    private fun createLobbyPresenter(lobbyId: String): LobbyPresenter = LobbyPresenter(LobbyView(lobbyId), this)

    private fun createGamePresenter(): GamePresenter = GamePresenter(this, assetManager)

    private fun createSettingsPresenter(): SettingsPresenter = SettingsPresenter(SettingsView(), this)

    private fun createEndGamePresenter(didWin: Boolean): EndGamePresenter = EndGamePresenter(EndGameView(), didWin, this)

    fun navigateToMenu() {
        PresenterManager.set(createMenuPresenter())
    }

    fun navigateToJoinGame() {
        PresenterManager.push(createJoinGamePresenter())
    }

    fun navigateToCreateLobby(lobbyId: String) {
        PresenterManager.push(createLobbyPresenter(lobbyId))
    }

    fun navigateToLobby(lobbyId: String) {
        if (PresenterManager.getCurrent() is JoinGamePresenter || PresenterManager.getCurrent() is EndGamePresenter) {
            PresenterManager.set(createLobbyPresenter(lobbyId))
        } else {
            PresenterManager.push(createLobbyPresenter(lobbyId))
        }
    }

    fun navigateToGame() {
        if (PresenterManager.getCurrent() is LobbyPresenter) {
            PresenterManager.set(createGamePresenter())
        } else {
            PresenterManager.push(createGamePresenter())
        }
    }

    fun navigateToSettings() {
        PresenterManager.push(createSettingsPresenter())
    }

    fun navigateToEndGame(didWin: Boolean) {
        if (PresenterManager.getCurrent() is GamePresenter) {
            PresenterManager.set(createEndGamePresenter(didWin))
        } else {
            PresenterManager.push(createEndGamePresenter(didWin))
        }
    }

    fun goBack() {
        print("${PresenterManager.getCurrent()} before")
        if (!PresenterManager.isEmpty()) {
            PresenterManager.pop()
        }
        if (PresenterManager.isEmpty()) {
            PresenterManager.push(createMenuPresenter())
        }
        print("${PresenterManager.getCurrent()} after")
    }
}
