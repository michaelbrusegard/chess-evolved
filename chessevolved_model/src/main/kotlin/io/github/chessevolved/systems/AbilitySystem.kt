package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityTriggerComponent
import io.github.chessevolved.components.BlockedComponent
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.VisualEffectComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.AbilityType
import io.github.chessevolved.enums.VisualEffectSize
import io.github.chessevolved.enums.VisualEffectType
import io.github.chessevolved.singletons.ECSEngine

class AbilitySystem :
    IteratingSystem(
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

        when (abilityComponent.ability) {
            AbilityType.EXPLOSION -> {
                // Since the trigger doesn't know if it was an active call or passive call.
                // It is the ability's job to determine.
                if (CapturedComponent.mapper.get(entity) == null) {
                    triggerExplosionEffect(entity, abilityTriggerComponent.targetPosition)
                }
            }
            AbilityType.SHIELD -> {
                triggerShieldEffect(entity, abilityTriggerComponent.targetPosition, abilityTriggerComponent.oldPosition, abilityComponent)
            }
            AbilityType.NEW_MOVEMENT -> {}
            AbilityType.SWAP -> {}
            AbilityType.MIRROR -> {}
            else -> {}
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
                    ECSEngine
                        .getEntitiesFor(Family.all(PieceTypeComponent::class.java).get())
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
        oldPosition: Position,
        abilityComponent: AbilityComponent,
    ) {
        // Check if shield effect exists
        val shieldEffectEntity =
            ECSEngine
                .getEntitiesFor(Family.all(VisualEffectComponent::class.java, PositionComponent::class.java).get())
                .firstOrNull {
                    VisualEffectComponent.mapper.get(it).effectType == VisualEffectType.SHIELD_ACTIVE &&
                        PositionComponent.mapper.get(it).position == oldPosition
                }

        val isCaptured = CapturedComponent.mapper.get(entity) != null

        if (isCaptured && shieldEffectEntity != null) {
            entity?.remove(BlockedComponent::class.java)
            entity?.remove(CapturedComponent::class.java)

            // Give duration such that it gets deleted
            VisualEffectComponent.mapper.get(shieldEffectEntity).duration = 0.1f
            abilityComponent.currentAbilityCDTime = abilityComponent.abilityCooldownTime

            // Start animation for shield break
            val effectEntity = ECSEngine.createEntity()
            effectEntity.add(VisualEffectComponent(VisualEffectType.SHIELD_BREAK, 3, squareSize = VisualEffectSize.NORMAL))
            effectEntity.add(HighlightComponent(Color.WHITE))
            effectEntity.add(PositionComponent(targetPosition))
            ECSEngine.addEntity(effectEntity)
        } else if (shieldEffectEntity != null) {
            abilityComponent.currentAbilityCDTime = 0
            PositionComponent.mapper.get(shieldEffectEntity).position = targetPosition
        } else {
            abilityComponent.currentAbilityCDTime = 0
            entity?.add(BlockedComponent())

            val effectEntity = ECSEngine.createEntity()
            effectEntity.add(VisualEffectComponent(VisualEffectType.SHIELD_ACTIVE, 3, duration = 0f, squareSize = VisualEffectSize.NORMAL))
            effectEntity.add(HighlightComponent(Color.WHITE))
            effectEntity.add(PositionComponent(targetPosition))
            ECSEngine.addEntity(effectEntity)
        }
    }
}
