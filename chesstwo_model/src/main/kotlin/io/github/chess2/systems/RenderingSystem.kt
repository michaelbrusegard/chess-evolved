package io.github.chess2.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chess2.components.BoardSizeComponent
import io.github.chess2.components.ChessBoardSpriteComponent
import io.github.chess2.components.SpriteComponent
import io.github.chess2.entities.ChessBoard

// This class should perhaps be placed in the view?
// Since it does the work of representing through libgdx
// View Module will come later
class RenderingSystem {
    private val camera: OrthographicCamera = OrthographicCamera()
    private val batch: SpriteBatch = SpriteBatch()

    init {
        camera.setToOrtho(false,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat())
        camera.update()
    }

    fun render(sprites: List<SpriteComponent>) {
        batch.projectionMatrix = camera.combined
        batch.begin()

        for (sprite: SpriteComponent in sprites) {
            sprite.sprite.draw(batch)
        }

        batch.end()
    }

    fun renderChessBoard(chessBoard: ChessBoard) {
        val boardSize: Int = chessBoard.getComponent(BoardSizeComponent::class.java).boardSize
        val sprites: ChessBoardSpriteComponent = chessBoard.getComponent(ChessBoardSpriteComponent::class.java)

        batch.projectionMatrix = camera.combined
        batch.begin()

        // Have to also determine if it is white side or black side.
        // White side starts with a black tile. TODO
        for (i in 0..boardSize-1) {
            for (j in 0..boardSize-1) {
                if ((i + j) % 2 == 0) {
                    // This is how you draw and scale sprites.
                    sprites.blackTileSprite.setScale(2.0f)
                    sprites.blackTileSprite.setPosition(j * 32.0f * 2.0f, i * 32.0f * 2.0f)
                    sprites.blackTileSprite.draw(batch)
                } else {
                    sprites.whiteTileSprite.setScale(2.0f)
                    sprites.whiteTileSprite.setPosition(j * 32.0f * 2.0f, i * 32.0f * 2.0f)
                    sprites.whiteTileSprite.draw(batch)
                }
            }
        }

        batch.end()
    }
}
