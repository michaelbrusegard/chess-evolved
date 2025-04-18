package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.AbilityTriggerComponent
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.BlockedComponent
import io.github.chessevolved.components.CanBeCapturedComponent
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.ValidMovesComponent
import io.github.chessevolved.singletons.ECSEngine

class CaptureSystem : IteratingSystem(
    Family.all(CapturedComponent::class.java).get(),
) {
    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
        val capturedPosition = PositionComponent.mapper.get(entity).position
        val capturedByAbility = CapturedComponent.mapper.get(entity).capturedByAbility

        val capturedBlockedComponent = BlockedComponent.mapper.get(entity)

        // Trigger the movementSystem to move the entity that captured.
        val capturingPiece =
            ECSEngine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get()).find { piece ->
                piece.getComponent(SelectionComponent::class.java) != null
            }

        ECSEngine.getEntitiesFor(Family.all(CanBeCapturedComponent::class.java).get()).map { piece ->
            piece.remove(CanBeCapturedComponent::class.java)
        }

        if (capturedBlockedComponent != null) {
            // Trigger the ability.
            entity?.add(AbilityTriggerComponent(capturedPosition, false))
            // entity?.remove(CapturedComponent::class.java)
            capturingPiece?.remove(SelectionComponent::class.java)
            capturingPiece?.remove(ValidMovesComponent::class.java)
            return
        }

        if (!capturedByAbility) {
            capturingPiece?.add(MovementIntentComponent(capturedPosition))
        } else {
            capturingPiece?.remove(SelectionComponent::class.java)
            capturingPiece?.remove(ValidMovesComponent::class.java)
        }

        val actor = ActorComponent.mapper.get(entity).actor
        actor.remove()

        ECSEngine.removeEntity(entity)
    }
}
