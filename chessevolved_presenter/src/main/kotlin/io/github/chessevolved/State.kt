import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class State {
    protected abstract fun handleInput()

    abstract fun update(dt: Float)

    abstract fun render(sb: SpriteBatch)

    abstract fun dispose()
}
