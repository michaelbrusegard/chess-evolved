package io.github.chessevolved

import io.github.chessevolved.presenters.*
import io.github.chessevolved.views.*

class Navigator {
    private fun createMenuPresenter(): MenuPresenter = MenuPresenter(MenuView(), this)
    private fun createJoinGamePresenter(): JoinGamePresenter = JoinGamePresenter(JoinGameView(), this)
    private fun createLobbyPresenter(lobbyId: String): LobbyPresenter = LobbyPresenter(LobbyView(lobbyId), this)
    private fun createGamePresenter(): GamePresenter = GamePresenter(GameView(), this)
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
        if (PresenterManager.getCurrent() is JoinGamePresenter) {
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
        if (!PresenterManager.isEmpty()) {
            PresenterManager.pop()
        }
        if (PresenterManager.isEmpty()) {
            PresenterManager.push(createMenuPresenter())
        }
    }
}
