package io.github.chessevolved.presenters

import io.github.chessevolved.views.EndGameView

class EndGamePresenter(
    private val endGameView: EndGameView,
    private val endGameStatus: Boolean,
) : IPresenter {
    init {
        endGameView.endGameStatus = endGameStatus
        endGameView.init()
    }

    /**
     * Returns to the MenuPresenter
     */
    fun returnToMenu() {
        print("Returning to menu...")
    }

    /**
     * Restarts Game
     */
    fun rematch() {
        print("Requesting rematch...")
    }

    override fun render() {
        endGameView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        endGameView.resize(width, height)
    }

    override fun dispose() {
        endGameView.dispose()
    }

    override fun setInputProcessor() {
        endGameView.setInputProcessor()
    }
}
