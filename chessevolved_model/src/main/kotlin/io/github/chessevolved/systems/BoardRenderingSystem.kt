package io.github.chessevolved.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.components.SpriteComponent

class BoardRenderingSystem(
    private val batch: SpriteBatch,
    private val boardWorldSize: Int,
) : EntitySystem() {
    private val blackTileSprite = SpriteComponent("board/black-tile.png").sprite
    private val whiteTileSprite = SpriteComponent("board/white-tile.png").sprite

    init {
        blackTileSprite.setSize(1f, 1f)
        whiteTileSprite.setSize(1f, 1f)
    }

    override fun update(deltaTime: Float) {
        for (y in 0 until boardWorldSize) {
            for (x in 0 until boardWorldSize) {
                val sprite =
                    if ((x + y) % 2 == 0) {
                        blackTileSprite
                    } else {
                        whiteTileSprite
                    }

                sprite.setPosition(x.toFloat(), y.toFloat())
                sprite.draw(batch)
            }
        }
    }
}
