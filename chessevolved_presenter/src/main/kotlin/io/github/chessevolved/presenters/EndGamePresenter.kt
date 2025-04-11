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
        endGameView.onReturnToMenuClicked = { navigator.goBack() }
        // We need to get the current lobby ID
        endGameView.onRematchClicked = { navigator.navigateToLobby("222222") }
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
