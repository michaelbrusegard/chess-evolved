package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import io.github.chessevolved.presenters.GamePresenter
import io.github.chessevolved.presenters.JoinGamePresenter
import io.github.chessevolved.views.AndroidView
import io.github.chessevolved.views.JoinGameViewImpl
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync

class ChessEvolvedGame : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        // addScreen(FirstScreen())
        addScreen(JoinGameScreen())
        setScreen<JoinGameScreen>()
    }
}

class FirstScreen : KtxScreen {
    // Temporary current presenter. Should be replaced with the state manager.
    val presenter: GamePresenter = GamePresenter(AndroidView())

    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.23f)
        presenter.render()
    }
}

// Testing join game view
class JoinGameScreen : KtxScreen {
    private val view: JoinGameViewImpl
    private lateinit var presenter: JoinGamePresenter

    init {
        view =
            JoinGameViewImpl(
                onJoinButtonClicked = {
                    presenter.onJoinButtonPressed()
                },
                onBackButtonClicked = {
                    presenter.returnToMenu()
                },
            )

        presenter = JoinGamePresenter(view)

        // Setup input handling
        Gdx.input.inputProcessor =
            object : InputAdapter() {
                override fun touchDown(
                    screenX: Int,
                    screenY: Int,
                    pointer: Int,
                    button: Int,
                ): Boolean {
                    view.handleInput(screenX, screenY)
                    return true
                }

                override fun keyTyped(character: Char): Boolean {
                    view.handleKeyTyped(character)
                    return true
                }
            }
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.23f)
        presenter.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        view.resize(width, height)
    }

    override fun dispose() {
        view.closeView()
    }
}
