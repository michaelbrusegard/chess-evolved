package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.views.IView

class MockPresenter(
    private val view: IView,
    private val navigator: Navigator,
) : IPresenter {
    override fun render(sb: SpriteBatch) {
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
    }

    override fun dispose() {
    }

    override fun setInputProcessor() {
    }
}
