package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.chessevolved.presenters.GamePresenter

class DesktopView : IView{
    private val boardCamera: OrthographicCamera = OrthographicCamera()
    private lateinit var boardViewport: FitViewport
    private val batch: SpriteBatch = SpriteBatch()
    private val gamePresenter: GamePresenter = GamePresenter()
    private val pieceSprites: List<Sprite> = gamePresenter.getPieceSprites()
    // First is black, second is white
    private val boardSprites: List<Sprite> = gamePresenter.getBoardSprites()
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()

    init {
        val scale: Int = gamePresenter.boardSize
        val size: Float = 32f * scale

        boardCamera.setToOrtho(false, size, size)

        boardViewport = FitViewport(size, size, boardCamera)
        boardViewport.setScreenBounds(
            ((Gdx.graphics.width - size * 2f) / 2f).toInt(),
            ((Gdx.graphics.height - size * 2f) / 2f).toInt(),
            size.toInt() * 2,
            size.toInt() * 2)

        // 0.0, 0.0, 0.0 is the viewports origin (so it's middle)
        // Moving to the left is plus, and moving to the right is minus
        boardCamera.position.set(0.0f + size / 2f, 0.0f + size / 2f, 0.0f)
        boardCamera.update()
    }

    override fun render() {
        boardViewport.apply()
        batch.projectionMatrix = boardViewport.camera.combined
        batch.begin()

        // Render board first
        renderBoard()
        renderPieces()

        batch.end()
    }

    private fun renderBoard() {
        //TODO:  Have to consider if white is first in the left corner or not.

        for (i in 0..gamePresenter.boardSize-1) {
            for (j in 0..gamePresenter.boardSize-1) {
                // Simple positioning so far.
                if ((i + j) % 2 == 0) {
                    boardSprites[0].setPosition(j * 32f, i * 32f)
                    boardSprites[0].draw(batch)
                } else {
                    boardSprites[1].setPosition(j * 32f, i * 32f)
                    boardSprites[1].draw(batch)
                }
            }
        }
    }

    private fun renderPieces() {
        for (sprite: Sprite in pieceSprites) {
            sprite.draw(batch)
        }
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }
}
