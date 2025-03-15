package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.chessevolved.presenters.JoinGamePresenter
import io.github.chessevolved.views.JoinGameView
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync
import ktx.scene2d.Scene2DSkin

class ChessEvolvedGame : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()
        val skin = Skin(Gdx.files.internal("skin/plain-james-ui.json"))
        Scene2DSkin.defaultSkin = skin

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val joinGameView =
        JoinGameView().apply {
            onJoinButtonClicked = { lobbyId ->
                joinGamePresenter.joinGame(lobbyId)
            }
            onReturnButtonClicked = {
                joinGamePresenter.returnToMenu()
            }
        }

    private val joinGamePresenter = JoinGamePresenter(joinGameView)

    // Commented out old presenter for testing joingame presenter
    // val presenter: GamePresenter = GamePresenter(AndroidView())

    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.23f)
        joinGamePresenter.render()
        // presenter.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        joinGamePresenter.resize(width, height)
    }

    override fun dispose() {
        joinGamePresenter.dispose()
    }
}
