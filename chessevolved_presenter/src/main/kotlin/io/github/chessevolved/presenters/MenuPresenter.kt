package io.github.chessevolved.presenters

import io.github.chessevolved.views.MenuView

class MenuPresenter(
    private val menuView: MenuView,
) : IPresenter {
    init {
        menuView.init()
        // The Presenter manager should activate the current input processor.
        // This here is temporary.
        setInputProcessor()
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
        println("Joined Game")
    }

    fun enterCreateGame() {
        println("Created Game")
    }
}
