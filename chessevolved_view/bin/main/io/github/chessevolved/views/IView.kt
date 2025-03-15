package io.github.chessevolved.views

import com.badlogic.gdx.graphics.g2d.Sprite

interface IView {
    /**
     * Updates the Viewport and Camera before starting a Sprite batch.
     */
    fun beginBatch()

    /**
     * Ends the batch sprite.
     */
    fun endBatch()

    /**
     * Render a specific sprite.
     * Position and other render data should be determined in the Sprite class.
     * @param sprite The sprite to be rendered.
     */
    fun render(sprite: Sprite)
}
