package io.github.chessevolved.presenters
import LobbyPresenter
import io.github.chessevolved.ScenePresenterStateManager
import io.github.chessevolved.views.JoinGameView
import io.github.chessevolved.views.LobbyView
import io.github.chessevolved.views.MenuView

class MenuPresenter(
    private val view: MenuView,
) : IPresenter {
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
        view.setInputProcessor()
    }

    fun enterJoinGame() {
        val joinGamePresenter = JoinGamePresenter(JoinGameView())
        ScenePresenterStateManager.push(StatePresenter(joinGamePresenter))
    }

    fun enterCreateGame() {
        // TODO: Create lobby code before showing lobby view
        val lobbyPresenter = LobbyPresenter(LobbyView("123ABC"))
        ScenePresenterStateManager.push(StatePresenter(lobbyPresenter))
    }
}
