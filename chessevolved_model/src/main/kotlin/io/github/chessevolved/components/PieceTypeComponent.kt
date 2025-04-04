package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

enum class PieceType {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING,
}

class PieceTypeComponent(
    var type: PieceType = PieceType.PAWN,
) : Component
