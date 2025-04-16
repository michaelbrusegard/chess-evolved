package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.ValidMovesComponent
import io.github.chessevolved.singletons.ECSEngine

class MovementSystem : IteratingSystem(
    Family.all(
        SelectionComponent::class.java,
        ValidMovesComponent::class.java,
        MovementIntentComponent::class.java,
    ).get(),
) {
    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
        val availableMoveSet = ValidMovesComponent.mapper.get(entity).validMoves.toSet()
        val targetPosition = MovementIntentComponent.mapper.get(entity).targetPosition

        if (!availableMoveSet.contains(targetPosition)) {
            return
        }

        val piecePositionComponent = PositionComponent.mapper.get(entity)
        val pieceActorComponent = ActorComponent.mapper.get(entity)
        val pieceMovementRuleComponent = MovementRuleComponent.mapper.get(entity)

        piecePositionComponent.position = targetPosition
        pieceActorComponent.actor.setPosition(targetPosition.x.toFloat(), targetPosition.y.toFloat())

        pieceMovementRuleComponent.getMovementRules().map {
            if (it.isFirstMove) {
                pieceMovementRuleComponent.removeMovementPattern(it.moveName)
            }
        }

        entity?.remove(SelectionComponent::class.java)
        entity?.remove(ValidMovesComponent::class.java)
        entity?.remove(MovementIntentComponent::class.java)
    }
}
