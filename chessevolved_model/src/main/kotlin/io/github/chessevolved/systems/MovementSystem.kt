package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.FowComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.ValidMovesComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.singletons.EcsEngine

class MovementSystem(
    private val onTurnComplete: () -> Unit,
) : IteratingSystem(
        Family
            .all(
                SelectionComponent::class.java,
                ValidMovesComponent::class.java,
                MovementIntentComponent::class.java,
            ).get(),
    ) {
    private val engine = EcsEngine

    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
        val availableMoveSet =
            ValidMovesComponent
                .mapper
                .get(entity)
                .validMoves
                .toSet()
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

        clearFow(targetPosition)

        entity?.remove(SelectionComponent::class.java)
        entity?.remove(ValidMovesComponent::class.java)
        entity?.remove(MovementIntentComponent::class.java)

        onTurnComplete()
    }

    private fun clearFow(center: Position) {
        for (x in -1..1) {
            for (y in -1..1) {
                val currentPosition = Position(center.x + x, center.y + y)

                val boardSquare =
                    engine.getEntitiesFor(
                        Family.all(PositionComponent::class.java, FowComponent::class.java).get(),
                    ).firstOrNull {
                        PositionComponent.mapper.get(it).position == currentPosition
                    }

                boardSquare?.let {
                    val fow = FowComponent.mapper.get(it)
                    if (fow.showFog) fow.showFog = false
                }
            }
        }
    }
}
