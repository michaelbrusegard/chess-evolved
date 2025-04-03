package io.github.chessevolved.presenters

import ScenePresenterStateManager
import io.github.chessevolved.views.JoinGameView
import io.github.chessevolved.views.MenuView

class MenuPresenter(
    private val view: MenuView
) : IPresenter {
    override fun render() {
        view.render()
    }

    override fun resize(width: Int, height: Int) {
        view.resize(width, height)
    }

    override fun dispose() {
        view.dispose()
    }

    fun enterJoinGame() {
        val joinGamePresenter = JoinGamePresenter(JoinGameView())
        ScenePresenterStateManager.push(StatePresenter(joinGamePresenter))
    }
}
