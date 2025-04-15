package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.serialization.GameStateSerializer
import io.github.chessevolved.singletons.ComponentMappers
import io.github.chessevolved.singletons.ECSEngine

class CaptureSystem : IteratingSystem(
    Family.all(CapturedComponent::class.java).get(),
) {
    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
        val capturedPosition = ComponentMappers.posMap.get(entity).position

        // Trigger the movementSystem to move the entity that captured.
        // TODO: do ability logic here for when a piece is captured.
        GameStateSerializer.getPieceEntities().find { entity ->
            entity.getComponent(SelectionComponent::class.java) != null
        }?.add(MovementIntentComponent(capturedPosition))

        ECSEngine.removeEntity(entity)
    }
}
