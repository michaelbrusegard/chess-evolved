package io.github.chessevolved

import io.github.chessevolved.presenters.JoinGamePresenter
import io.github.chessevolved.views.JoinGameView
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync

class ChessEvolvedGame : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val joinGameView = JoinGameView()
    private val joinGamePresenter = JoinGamePresenter(joinGameView)

    init {
        // Set up callbacks from view to presenter
        joinGameView.setJoinButtonCallback { joinGamePresenter.onJoinButtonPressed() }
        joinGameView.setBackButtonCallback { joinGamePresenter.returnToMenu() }
    }

    // Commented out original game presenter
    // val presenter: GamePresenter = GamePresenter(AndroidView())

    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.23f)
        // presenter.render() - commented out
        joinGamePresenter.render()
        joinGameView.render(delta)
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        joinGameView.resize(width, height)
    }

    override fun dispose() {
        joinGameView.dispose()
    }
}
