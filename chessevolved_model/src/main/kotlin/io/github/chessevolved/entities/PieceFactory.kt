package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent

class PieceFactory(
    private val engine: Engine,
) {
    fun createPiece(
        position: Position,
        pieceType: PieceType,
        playerColor: PlayerColor,
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(PieceTypeComponent(pieceType))
            add(PlayerColorComponent(playerColor))
            add(AbilityComponent(emptyList()))
            engine.addEntity(this)
        }

    fun createPawn(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.PAWN, color)

    fun createKnight(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.KNIGHT, color)

    fun createBishop(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.BISHOP, color)

    fun createRook(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.ROOK, color)

    fun createQueen(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.QUEEN, color)

    fun createKing(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.KING, color)
}
