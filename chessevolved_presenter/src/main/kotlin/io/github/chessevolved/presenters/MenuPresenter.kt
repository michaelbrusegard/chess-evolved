package io.github.chessevolved.presenters

import io.github.chessevolved.views.MenuView

class MenuPresenter(
    menuView: MenuView,
) : IPresenter {
    override fun render() {
        TODO("Not yet implemented")
    }

    fun enterJoinGame() {
        // TODO: Append JoinGamePresenter to the Stack.
    }

    fun enterCreateGame() {
        // TODO: Append CreateGamePresenter to the stack.
    }
}
