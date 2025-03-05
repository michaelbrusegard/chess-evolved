package io.github.chess2.systems

import io.github.chess2.components.BoardSizeComponent
import io.github.chess2.components.ChessBoardSpriteComponent
import io.github.chess2.components.PositionComponent
import io.github.chess2.components.SpriteComponent
import io.github.chess2.entities.ChessBoard
import io.github.chess2.entities.ChessPiece
import io.github.chess2.singletons.ECSEngine
import ktx.ashley.add

class GameBoardSetup {
    val pieces: MutableList<ChessPiece> = mutableListOf()
    val board: ChessBoard = ChessBoard()
    // Have a list of pieces for now, this should be established in the playersingleton later.

    init {
        // Test values for now
        for (i in 0..4) {
            val piece: ChessPiece = ChessPiece()
            piece.add(PositionComponent(i, i))
            piece.add(SpriteComponent("pieces/rookBlackExample.png", i * 32.0f, i * 32.0f))
            pieces.add(piece)
            ECSEngine.addEntity(piece)
        }

        board.add(BoardSizeComponent(8))
        board.add(ChessBoardSpriteComponent())
        ECSEngine.addEntity(board)
    }
}
