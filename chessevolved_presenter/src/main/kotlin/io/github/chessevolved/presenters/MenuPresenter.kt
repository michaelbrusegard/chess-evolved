package io.github.chessevolved.presenters
import io.github.chessevolved.ScenePresenterStateManager
import io.github.chessevolved.views.JoinGameView
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
    }
}
