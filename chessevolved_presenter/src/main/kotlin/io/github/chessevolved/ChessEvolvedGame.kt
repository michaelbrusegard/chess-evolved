package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.chessevolved.presenters.JoinGamePresenter
import io.github.chessevolved.presenters.MenuPresenter
import io.github.chessevolved.presenters.StatePresenter
import io.github.chessevolved.views.JoinGameView
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

        //TODO: Remove joingame as the the starting screen
        // Made the stack like this since JOIN GAME is the first screen to "usable"
        // and to test the state-chaning
        val menuPresenter = MenuPresenter(MenuView())
        ScenePresenterStateManage.push(StatePresenter(menuPresenter))
        val joinPresenter = JoinGamePresenter(JoinGameView())
        ScenePresenterStateManage.push(StatePresenter(joinPresenter))
    }

    override fun render() {
        clearScreen(red = 0.5f, green = 0.5f, blue = 0.75f)
        super.render()
        val delta = Gdx.graphics.deltaTime
        ScenePresenterStateManage.update(delta)
        ScenePresenterStateManage.render(SpriteBatch())
    }

    override fun dispose() {
        ScenePresenterStateManage.dispose()
    }
}
