package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import io.github.chessevolved.components.Position
import io.github.chessevolved.singletons.ComponentMappers

class MovementSystem {
    fun movePieceToPos(
        piece: Entity?,
        position: Position
    ) {
        if (piece == null) {
            throw NullPointerException("Piece is null")
        }

        val piecePositionComponent = ComponentMappers.posMap.get(piece)
        val actorComponent = ComponentMappers.actorMap.get(piece)

        piecePositionComponent.changePosition(position)
        actorComponent.actor.setPosition(position.x.toFloat(), position.y.toFloat())
    }
}
