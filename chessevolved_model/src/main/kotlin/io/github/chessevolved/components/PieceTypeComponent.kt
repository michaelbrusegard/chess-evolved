package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

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
) : Component {
    companion object {
        val mapper: ComponentMapper<PieceTypeComponent> =
            ComponentMapper.getFor(PieceTypeComponent::class.java)
    }
}
