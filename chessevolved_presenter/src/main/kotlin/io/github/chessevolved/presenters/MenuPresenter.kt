package io.github.chessevolved.presenters
import LobbyPresenter
import io.github.chessevolved.PresenterManager
import io.github.chessevolved.views.JoinGameView
import io.github.chessevolved.views.LobbyView
import io.github.chessevolved.views.MenuView

class MenuPresenter(
    private val menuView: MenuView,
) : IPresenter {
    init {
        menuView.onCreateLobbyButtonClicked = { enterCreateGame() }
        menuView.onJoinGameButtonClicked = { enterJoinGame() }
        menuView.init()
    }

    override fun render() {
        menuView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        menuView.resize(width, height)
    }

    override fun dispose() {
        menuView.dispose()
    }

    override fun setInputProcessor() {
        menuView.setInputProcessor()
    }

    fun enterJoinGame() {
        val joinGamePresenter = JoinGamePresenter(JoinGameView())
        PresenterManager.push(StatePresenter(joinGamePresenter))
    }

    fun enterCreateGame() {
        // TODO: Create lobby code before showing lobby view
        val lobbyPresenter = LobbyPresenter(LobbyView("123ABC"))
        PresenterManager.push(StatePresenter(lobbyPresenter))
    }
}
