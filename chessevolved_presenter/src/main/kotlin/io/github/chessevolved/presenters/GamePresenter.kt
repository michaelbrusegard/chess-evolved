package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.chessevolved.components.BoardSizeComponent
import io.github.chessevolved.components.ChessBoardSpriteComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SpriteComponent
import io.github.chessevolved.entities.ChessBoard
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.singletons.ECSEngine

class GamePresenter : IPresenter {
    // Have a list of pieces for now, this should be established in the playersingleton later.
    val pieces: MutableList<ChessPiece> = mutableListOf()
    val board: ChessBoard = ChessBoard()

    val boardSize: Int = 8

    init {
        // Test values for now
        for (i in 0..4) {
            val piece: ChessPiece = ChessPiece()
            piece.add(PositionComponent(i, i))
            piece.add(SpriteComponent("pieces/rookBlackExample.png", i * 32.0f, i * 32.0f))
            pieces.add(piece)
            ECSEngine.addEntity(piece)
        }

        board.add(BoardSizeComponent(boardSize))
        board.add(ChessBoardSpriteComponent())
        ECSEngine.addEntity(board)
    }

    fun getPieceSprites() : List<Sprite> {
        return pieces.map { it.getComponent(SpriteComponent::class.java).sprite }
    }

    fun getBoardSprites() : List<Sprite> {
        return listOf(board.getComponent(ChessBoardSpriteComponent::class.java).blackTileSprite,
                      board.getComponent(ChessBoardSpriteComponent::class.java).whiteTileSprite)
    }
}
