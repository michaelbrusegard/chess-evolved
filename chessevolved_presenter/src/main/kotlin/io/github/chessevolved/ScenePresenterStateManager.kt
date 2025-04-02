import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util.ArrayDeque

object ScenePresenterStateManager {
    private val states = ArrayDeque<State>()

    fun push(state: State) {
        states.push(state)
    }

    fun pop() {
        if (states.isNotEmpty()) {
            states.pop()
        }
    }

    fun set(state: State) {
        if (states.isNotEmpty()) {
            states.pop()
        }
        states.push(state)
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
