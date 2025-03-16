package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.presenters.GamePresenter
import io.github.chessevolved.views.AndroidView
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync

class ChessEvolvedGame : KtxGame<KtxScreen>() {
    // private lateinit var gsm: ScenePresenterStateManage //
    private lateinit var batch: SpriteBatch

    override fun create() {
        KtxAsync.initiate()

        batch = SpriteBatch()
        // gsm = ScenePresenterStateManage//

        addScreen(FirstScreen(batch)) // GamePresenter(AndroidView())//
        setScreen<FirstScreen>()
        ScenePresenterStateManage.push(GamePresenter(AndroidView()))
    }

    override fun render() {
        super.render()
        val delta = Gdx.graphics.deltaTime
        ScenePresenterStateManage.update(delta)
        ScenePresenterStateManage.render(batch)
    }

    override fun dispose() {
        batch.dispose()
        // gsm.dispose()//
        ScenePresenterStateManage.dispose()
    }
}

class FirstScreen(
    // private val gsm: ScenePresenterStateManage,  Pass gsm to manage states//
    private val batch: SpriteBatch,
    // private val presenter: GamePresenter//
) : KtxScreen {
    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.23f)
        ScenePresenterStateManage.update(delta)
        ScenePresenterStateManage.render(batch)
        // presenter.render()
    }

    override fun dispose() {
        // Clean up resources if needed//
    }
}
