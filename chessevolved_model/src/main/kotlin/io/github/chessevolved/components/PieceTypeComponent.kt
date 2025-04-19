package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.enums.PieceType

class PieceTypeComponent(
    var type: PieceType = PieceType.PAWN,
) : Component {
    companion object {
        val mapper: ComponentMapper<PieceTypeComponent> =
            ComponentMapper.getFor(PieceTypeComponent::class.java)
    }
}
