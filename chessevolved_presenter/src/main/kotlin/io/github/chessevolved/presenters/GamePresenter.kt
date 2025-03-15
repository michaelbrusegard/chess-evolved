package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.chessevolved.components.BoardSizeComponent
import io.github.chessevolved.components.ChessBoardSpriteComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SpriteComponent
import io.github.chessevolved.entities.ChessBoard
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.views.IView

class GamePresenter(
    givenView: IView,
) : IPresenter {
    val pieces: MutableList<ChessPiece> = mutableListOf()
    val board: ChessBoard = ChessBoard()

    val boardViewportSize: Float = Gdx.graphics.width - 10f
    var boardScreenPosX: Int = 0
    var boardScreenPosY: Int = 0
    val pixelSize: Int = 32
    val view: IView = givenView

    // Temporary value, should be defined elsewhere
    val boardSize: Int = 8

    init {
        board.add(BoardSizeComponent(boardSize))
        board.add(ChessBoardSpriteComponent())

        val piece: ChessPiece = ChessPiece()
        piece.add(PositionComponent(4, 4))
        piece.add(SpriteComponent("pieces/rookBlackExample.png"))

        pieces.add(piece)

        boardScreenPosX = (Gdx.graphics.width - (boardSize * pixelSize)) / 2
        boardScreenPosY = (Gdx.graphics.height - (boardSize * pixelSize)) / 2
    }

    override fun render() {
        view.beginBatch()
        for (y in 0..boardSize - 1) {
            for (x in 0..boardSize - 1) {
                var sprite = board.getComponent(ChessBoardSpriteComponent::class.java).whiteTileSprite
                if ((y + x) % 2 == 0) {
                    sprite = board.getComponent(ChessBoardSpriteComponent::class.java).blackTileSprite
                }
                sprite.setPosition(boardScreenPosX.toFloat() + pixelSize * x, boardScreenPosY.toFloat() + pixelSize * y)
                view.render(sprite)
            }
        }

        val sprite: Sprite = pieces[0].getComponent(SpriteComponent::class.java).sprite
        val posComp: PositionComponent = pieces[0].getComponent(PositionComponent::class.java)
        sprite.setPosition(
            (boardScreenPosX + (posComp.xPos - 1) * pixelSize).toFloat(),
            (boardScreenPosY + (posComp.yPos - 1) * pixelSize).toFloat(),
        )
        view.render(sprite)

        view.endBatch()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
    }

    override fun dispose() {
    }
}
