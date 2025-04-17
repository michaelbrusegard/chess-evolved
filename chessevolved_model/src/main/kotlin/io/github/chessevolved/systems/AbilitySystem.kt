package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityTriggerComponent
import io.github.chessevolved.components.AbilityType
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.singletons.ECSEngine

class AbilitySystem : IteratingSystem(
    Family.all(AbilityComponent::class.java, AbilityTriggerComponent::class.java).get()
)
{
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val abilityComponent = AbilityComponent.mapper.get(entity)
        val abilityTriggerComponent = AbilityTriggerComponent.mapper.get(entity)

        when (abilityComponent.ability) {
            AbilityType.EXPLOSION -> {
                if (abilityComponent.currentAbilityCDTime <= 0) {
                    abilityComponent.currentAbilityCDTime = abilityComponent.abilityCooldownTime
                    triggerExplosionEffect(entity, abilityTriggerComponent.position)
                } else {
                    abilityComponent.currentAbilityCDTime--
                }
            }
            AbilityType.SWAP -> {}
            AbilityType.SHIELD -> {}
            AbilityType.MIRROR -> {}
            AbilityType.NEW_MOVEMENT -> {}
        }

        entity?.remove(AbilityTriggerComponent::class.java)
    }

    private fun triggerExplosionEffect(entity: Entity?, targetPosition: Position) {
        println("Explosion Triggered")
        // From target position.
        // Hard coded values define the Explosion
        // Search from here all the pieces on positions in the radius.
        // Add an animation component which either the rendering system or an own system handles.

        // Explodes the 8 squares around it (fair) will capture immediately
        val radius = 1

        for (i in -radius..radius) {
            for (j in -radius..radius) {
                val position = Position(targetPosition.x + j, targetPosition.y + i)
                val pieceColor = PlayerColorComponent.mapper.get(entity).color
                val capturingPiece = ECSEngine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get())
                    .firstOrNull() {
                        PlayerColorComponent.mapper.get(it).color != pieceColor &&
                            PositionComponent.mapper.get(it).position == position
                    }

                println("Capturing Piece: $capturingPiece")
                println("Capturing at position from explosion: $position")

                capturingPiece?.add(CapturedComponent(capturedByAbility = true))
            }
        }
    }
}
