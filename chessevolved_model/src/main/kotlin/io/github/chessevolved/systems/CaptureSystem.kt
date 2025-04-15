package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.singletons.ECSEngine

class CaptureSystem : IteratingSystem(
    Family.all(CapturedComponent::class.java).get(),
) {
    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
        val capturedPosition = PositionComponent.mapper.get(entity).position

        // Trigger the movementSystem to move the entity that captured.
        // TODO: do ability logic here for when a piece is captured.
        ECSEngine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get()).find { entity ->
            entity.getComponent(SelectionComponent::class.java) != null
        }?.add(MovementIntentComponent(capturedPosition))

        ECSEngine.removeEntity(entity)
    }
}
