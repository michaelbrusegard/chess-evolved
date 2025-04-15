package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.gdx.graphics.Color
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.ValidMovesComponent
import io.github.chessevolved.serialization.GameStateSerializer
import io.github.chessevolved.singletons.ComponentMappers

class SelectionEntityListener(private val boardSize: Int) : EntityListener {
    private val moveValidator = MoveValidator()

    override fun entityAdded(entity: Entity?) {
        val availablePositions =
            moveValidator.checkAvailablePositions(
                ComponentMappers.colorMap.get(entity).color,
                ComponentMappers.posMap.get(entity).position,
                MovementRuleComponent.mapper.get(entity),
                boardSize,
            )

        val availablePositionsSet = availablePositions.toSet()

        for (boardSquare in GameStateSerializer.getBoardSquareEntities()) {
            if (availablePositionsSet.contains(ComponentMappers.posMap.get(boardSquare).position)) {
                boardSquare.add(HighlightComponent(Color(0.5f, 0.5f, 0.5f, 1f)))
            }
        }

        entity?.add(ValidMovesComponent(availablePositions))
    }

    override fun entityRemoved(entity: Entity?) {
        entity?.remove(ValidMovesComponent::class.java)

        for (boardSquare in GameStateSerializer.getBoardSquareEntities()) {
            boardSquare.remove(HighlightComponent::class.java)
        }
    }
}
