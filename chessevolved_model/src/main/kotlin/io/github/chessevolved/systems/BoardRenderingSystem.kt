package io.github.chessevolved.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.components.SpriteComponent

class BoardRenderingSystem(
    private val batch: SpriteBatch,
    private val boardSize: Int,
    private val pixelSize: Int,
    private val boardScreenPosX: Int,
    private val boardScreenPosY: Int,
) : EntitySystem() {
    override fun update(deltaTime: Float) {
        for (y in 0 until boardSize) {
            for (x in 0 until boardSize) {
                val sprite =
                    if ((x + y) % 2 == 0) {
                        SpriteComponent("board/black-tile.png").sprite
                    } else {
                        SpriteComponent("board/white-tile.png").sprite
                    }

                sprite.setPosition(
                    boardScreenPosX + x * pixelSize.toFloat(),
                    boardScreenPosY + y * pixelSize.toFloat(),
                )
                sprite.setSize(pixelSize.toFloat(), pixelSize.toFloat())
                sprite.draw(batch)
            }
        }
    }
}
