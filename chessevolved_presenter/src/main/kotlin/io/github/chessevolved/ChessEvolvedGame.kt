package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.chessevolved.presenters.MenuPresenter
import io.github.chessevolved.presenters.StatePresenter
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

        val menuView = MenuView()
        menuView.init()
        val menuPresenter = MenuPresenter(menuView)

        menuView.onCreateLobbyButtonClicked = { menuPresenter.enterCreateGame() }
        menuView.onJoinGameButtonClicked = { menuPresenter.enterJoinGame() }

        PresenterManager.push(StatePresenter(menuPresenter))
    }

    override fun render() {
        clearScreen(red = 0.5f, green = 0.5f, blue = 0.75f)
        val delta = Gdx.graphics.deltaTime
        PresenterManager.update(delta)
        PresenterManager.render(SpriteBatch())
    }

    override fun dispose() {
        PresenterManager.dispose()
    }
}
