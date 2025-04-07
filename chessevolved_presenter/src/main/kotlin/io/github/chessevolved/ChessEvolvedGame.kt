package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync
import ktx.scene2d.Scene2DSkin

class ChessEvolvedGame : KtxGame<KtxScreen>() {
    private lateinit var batch: SpriteBatch
    private lateinit var skin: Skin
    private lateinit var navigator: Navigator

    override fun create() {
        KtxAsync.initiate()
        batch = SpriteBatch()
        skin = Skin(Gdx.files.internal("skin/plain-james-ui.json"))
        Scene2DSkin.defaultSkin = skin
        navigator = Navigator()
        navigator.navigateToMenu()
    }

    override fun render() {
        clearScreen(red = 0.5f, green = 0.5f, blue = 0.75f)
        PresenterManager.update(Gdx.graphics.deltaTime)
        PresenterManager.render(batch)
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        PresenterManager.getCurrent()?.resize(width, height)
        super.resize(width, height)
    }

    override fun dispose() {
        PresenterManager.dispose()
        batch.dispose()
        skin.dispose()
    }
}
