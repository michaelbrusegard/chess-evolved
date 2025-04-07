package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.views.EndGameView

class EndGamePresenter(
    private val endGameView: EndGameView,
    private val endGameStatus: Boolean,
    private val navigator: Navigator,
) : IPresenter {
    init {
        endGameView.endGameStatus = endGameStatus
        endGameView.init()
        endGameView.onReturnToMenuClicked = { returnToMenu() }
        endGameView.onRematchClicked = { rematch() }
    }

    fun returnToMenu() {
        navigator.goBack()
    }

    fun rematch() {
        // TODO: Implement rematch logic (likely involves navigator)
        // Example: navigator.navigateToGame() or similar
    }

    override fun update(dt: Float) {
        // No update logic needed for this simple screen yet
    }

    override fun render(sb: SpriteBatch) {
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
