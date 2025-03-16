package io.github.chess2.States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util.ArrayDeque

class ScenePresenterStateManage {

    private val states = ArrayDeque<State>()

    fun push(state: State) {
        states.push(state)
    }

    fun pop() {
        states.pop()
    }

    fun set(state: State) {
        states.pop()
        states.push(state)
    }

    fun update(dt: Float) {
        states.peek().update(dt)
    }

    fun render(sb: SpriteBatch) {
        states.peek().render(sb)
    }

    fun dispose() {
        states.forEach { it.dispose() }
    }
}
