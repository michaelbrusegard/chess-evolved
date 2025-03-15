package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.chessevolved.presenters.GamePresenter
import io.github.chessevolved.views.AndroidView
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
    // Temporary current presenter. Should be replaced with the state manager.
    val presenter: GamePresenter = GamePresenter(AndroidView())

    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.23f)
        presenter.render()
    }
}
