package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.chessevolved.presenters.MenuPresenter
import io.github.chessevolved.views.MenuView
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
    // We create the view outside of the presenter so that it is easier to test the presenter with a mock view

    // First screen to always be displayed is the Menu Screen.
    // Future discussion, should we have a loading screen showing credits at the start?
    private val menuView = MenuView()

    private val menuPresenter = MenuPresenter(menuView)

    init {
        menuView.apply {
            onCreateLobbyButtonClicked = { menuPresenter.enterCreateGame() }
            onJoinGameButtonClicked = { menuPresenter.enterJoinGame() }
        }
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.5f, green = 0.5f, blue = 0.75f)
        menuPresenter.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        menuPresenter.resize(width, height)
    }

    override fun dispose() {
        menuPresenter.dispose()
    }
}
