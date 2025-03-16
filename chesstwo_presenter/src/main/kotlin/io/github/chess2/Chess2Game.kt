package io.github.chess2

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chess2.States.ScenePresenterStateManage
import io.github.chess2.States.SplashState
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class Chess2Game : KtxGame<KtxScreen>() {

    private lateinit var gsm: ScenePresenterStateManage
    private lateinit var batch: SpriteBatch

    override fun create() {
        KtxAsync.initiate()

        batch = SpriteBatch()
        gsm = ScenePresenterStateManage()
        gsm.push(SplashState(gsm, batch))
    }

    override fun render() {
        super.render()
        val delta = Gdx.graphics.deltaTime
        gsm.update(delta)
        gsm.render(batch)
    }

    override fun dispose() {
        batch.dispose()
        gsm.dispose()
    }
}
