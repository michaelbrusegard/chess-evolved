package io.github.chess2.States

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.graphics.use

class SplashState(gsm: ScenePresenterStateManage, private val batch: SpriteBatch) : State(gsm) {
    private val splashImage = Texture("logo.png".toInternalFile())
    private var elapsedTime = 0f

    override fun handleInput() {
    }

    override fun update(dt: Float) {
        elapsedTime += dt
        if (elapsedTime > 2f) {
            gsm.set(MenuState(gsm, batch))
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.use {
            it.draw(splashImage, 100f, 160f)
        }
    }

    override fun dispose() {
        splashImage.disposeSafely()
    }
}
