package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityTriggerComponent
import io.github.chessevolved.components.AbilityType
import io.github.chessevolved.components.BlockedComponent
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.VisualEffectComponent
import io.github.chessevolved.components.VisualEffectSize
import io.github.chessevolved.components.VisualEffectType
import io.github.chessevolved.singletons.ECSEngine

class AbilitySystem : IteratingSystem(
    Family.all(AbilityComponent::class.java, AbilityTriggerComponent::class.java).get(),
) {
    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
        val abilityComponent = AbilityComponent.mapper.get(entity)
        val abilityTriggerComponent = AbilityTriggerComponent.mapper.get(entity)

        if (abilityComponent.currentAbilityCDTime <= 0) {
            abilityComponent.currentAbilityCDTime = abilityComponent.abilityCooldownTime
        } else {
            abilityComponent.currentAbilityCDTime--
            entity?.remove(AbilityTriggerComponent::class.java)
            return
        }

        if (abilityTriggerComponent.active) {
            // Active abilities:
            when (abilityComponent.ability) {
                AbilityType.EXPLOSION -> {
                    triggerExplosionEffect(entity, abilityTriggerComponent.position)
                }
                AbilityType.SWAP -> {}
                AbilityType.MIRROR -> {}
                else -> {}
            }
        } else {
            // Passive abilities
            when (abilityComponent.ability) {
                AbilityType.SHIELD -> {
                    triggerShieldEffect(entity, abilityTriggerComponent.position, abilityComponent)
                }
                AbilityType.NEW_MOVEMENT -> {}
                else -> {}
            }
        }

        entity?.remove(AbilityTriggerComponent::class.java)
    }

    private fun triggerExplosionEffect(
        entity: Entity?,
        targetPosition: Position,
    ) {
        val radius = 1

        val effectEntity = ECSEngine.createEntity()
        effectEntity.add(VisualEffectComponent(VisualEffectType.EXPLOSION, 6, squareSize = VisualEffectSize.MEDIUM))
        effectEntity.add(HighlightComponent(Color.WHITE))
        effectEntity.add(PositionComponent(targetPosition))
        ECSEngine.addEntity(effectEntity)

        for (i in -radius..radius) {
            for (j in -radius..radius) {
                val position = Position(targetPosition.x + j, targetPosition.y + i)
                val pieceColor = PlayerColorComponent.mapper.get(entity).color
                val capturingPiece =
                    ECSEngine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get())
                        .firstOrNull {
                            PlayerColorComponent.mapper.get(it).color != pieceColor &&
                                PositionComponent.mapper.get(it).position == position
                        }

                capturingPiece?.add(CapturedComponent(capturedByAbility = true))
            }
        }
    }

    private fun triggerShieldEffect(
        entity: Entity?,
        targetPosition: Position,
        abilityComponent: AbilityComponent,
    ) {
        // Check if shield effect exists
        val shieldEffectEntity = ECSEngine.getEntitiesFor(Family.all(VisualEffectComponent::class.java, PositionComponent::class.java).get())
            .firstOrNull {
                VisualEffectComponent.mapper.get(it).effectType == VisualEffectType.SHIELD_ACTIVE &&
                        PositionComponent.mapper.get(it).position == targetPosition
            }

        val isCaptured = CapturedComponent.mapper.get(entity) != null

        if (isCaptured && shieldEffectEntity != null) {
            entity?.remove(BlockedComponent::class.java)
            entity?.remove(CapturedComponent::class.java)

            // Give duration such that it gets deleted
            VisualEffectComponent.mapper.get(shieldEffectEntity).duration = 0.1f
            abilityComponent.currentAbilityCDTime = abilityComponent.abilityCooldownTime
            // Start animation for shield break
        } else if (shieldEffectEntity == null) {
            entity?.add(BlockedComponent())
            val effectEntity = ECSEngine.createEntity()
            effectEntity.add(VisualEffectComponent(VisualEffectType.SHIELD_ACTIVE, 3, duration = 0f, squareSize = VisualEffectSize.NORMAL))
            effectEntity.add(HighlightComponent(Color.WHITE))
            effectEntity.add(PositionComponent(targetPosition))
            ECSEngine.addEntity(effectEntity)
        }
    }
}
