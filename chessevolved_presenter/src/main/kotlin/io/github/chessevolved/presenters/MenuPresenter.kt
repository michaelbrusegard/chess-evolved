package io.github.chessevolved.presenters

import ScenePresenterStateManage
import SettingsView
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
        ScenePresenterStateManage.push(StatePresenter(joinGamePresenter))
    }

    fun enterSettings() {
        val settingsPresenter = SettingsPresenter(SettingsView())
        ScenePresenterStateManage.push(StatePresenter(settingsPresenter))
    }
}
