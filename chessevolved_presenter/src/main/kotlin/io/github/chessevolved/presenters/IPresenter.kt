package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Interface for all presenters.
 * Presenters connect models to views and contain application logic.
 */
interface IPresenter {
    /**
     * Updates the presenter's logic.
     * Called on each game loop cycle before rendering.
     */
    fun update(dt: Float)

    /**
     * Renders the presenter's view.
     * Called on each render cycle.
     */
    fun render(sb: SpriteBatch)

    /**
     * Handles screen size changes.
     */
    fun resize(
        width: Int,
        height: Int,
    )

    /** Releases resources used by the presenter. */
    fun dispose()

    /** Sets the presenter's view as the current input processor. */
    fun setInputProcessor()
}
