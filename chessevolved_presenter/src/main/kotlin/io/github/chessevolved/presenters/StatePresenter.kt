package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.State

class StatePresenter(
    private val presenter: IPresenter,
) : State() {
    override fun handleInput() {
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        presenter.render()
    }

    override fun dispose() {
        presenter.dispose()
    }

    override fun setInputProcessor() {
        presenter.setInputProcessor()
    }
}
