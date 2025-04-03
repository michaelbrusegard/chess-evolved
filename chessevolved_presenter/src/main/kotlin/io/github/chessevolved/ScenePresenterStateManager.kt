package io.github.chessevolved

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util.ArrayDeque

object ScenePresenterStateManager {
    private val states = ArrayDeque<State>()

    fun push(state: State) {
        states.push(state)
        state.setInputProcessor()
    }

    fun pop() {
        if (states.isNotEmpty()) {
            states.pop()
        }
        if (states.isEmpty()) return
        states.peekFirst().setInputProcessor()
    }

    fun update(dt: Float) {
        if (states.isNotEmpty()) {
            states.peek().update(dt)
        }
    }

    fun render(sb: SpriteBatch) {
        if (states.isNotEmpty()) {
            states.peek().render(sb)
        }
    }

    fun dispose() {
        states.forEach { it.dispose() }
    }
}
