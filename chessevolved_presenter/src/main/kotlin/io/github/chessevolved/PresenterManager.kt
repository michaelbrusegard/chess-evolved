package io.github.chessevolved

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.presenters.IPresenter
import java.util.ArrayDeque

object PresenterManager {
    private val presenters = ArrayDeque<IPresenter>()

    fun push(presenter: IPresenter) {
        presenters.push(presenter)
        presenter.setInputProcessor()
    }

    fun pop() {
        if (presenters.isNotEmpty()) {
            presenters.pop().dispose()
        }
        presenters.peekFirst()?.setInputProcessor()
    }

    fun set(presenter: IPresenter) {
        if (presenters.isNotEmpty()) {
            presenters.pop().dispose()
        }
        push(presenter)
    }

    fun render(sb: SpriteBatch) {
        presenters.peekFirst()?.render(sb)
    }

    fun dispose() {
        presenters.forEach { it.dispose() }
        presenters.clear()
    }

    fun getCurrent(): IPresenter? = presenters.peekFirst()

    fun isEmpty(): Boolean = presenters.isEmpty()
}
