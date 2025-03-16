package io.github.chess2.States
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MenuState(gsm: ScenePresenterStateManage, private val batch: SpriteBatch) : State(gsm) {

    init {
        createUI()
    }

    override fun handleInput() {
    }

    override fun update(dt: Float) {
    }

    override fun render(sb: SpriteBatch) {
    }

    override fun dispose() {
    }

    private fun createUI() {
    }
}

