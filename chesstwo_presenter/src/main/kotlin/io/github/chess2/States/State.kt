package io.github.chess2.States

import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class State(protected val gsm: ScenePresenterStateManage) {

    protected abstract fun handleInput()

    abstract fun update(dt: Float)

    abstract fun render(sb: SpriteBatch)
    abstract fun dispose()
}

