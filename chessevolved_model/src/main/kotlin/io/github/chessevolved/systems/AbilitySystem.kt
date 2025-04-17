package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
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
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.components.VisualEffectComponent
import io.github.chessevolved.components.VisualEffectType
import io.github.chessevolved.singletons.ECSEngine

class AbilitySystem(
    val assetManager: AssetManager
) : IteratingSystem(
    Family.all(AbilityComponent::class.java, AbilityTriggerComponent::class.java).get()
)
{
    init {
        val filePathPrefix = "ability/effects/"
        assetManager.load(filePathPrefix + "explosion.png", Texture::class.java)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
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

    private fun triggerExplosionEffect(entity: Entity?, targetPosition: Position) {
        val radius = 1

        val effectEntity = ECSEngine.createEntity()
        effectEntity.add(VisualEffectComponent(VisualEffectType.EXPLOSION, 1f))
        effectEntity.add(TextureRegionComponent(
            TextureRegion(assetManager.get("ability/effects/explosion.png", Texture::class.java))
        ))
        effectEntity.add(HighlightComponent(Color.WHITE))
        effectEntity.add(PositionComponent(targetPosition))
        ECSEngine.addEntity(effectEntity)

        for (i in -radius..radius) {
            for (j in -radius..radius) {
                val position = Position(targetPosition.x + j, targetPosition.y + i)
                val pieceColor = PlayerColorComponent.mapper.get(entity).color
                val capturingPiece = ECSEngine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get())
                    .firstOrNull() {
                        PlayerColorComponent.mapper.get(it).color != pieceColor &&
                            PositionComponent.mapper.get(it).position == position
                    }

                capturingPiece?.add(CapturedComponent(capturedByAbility = true))
            }
        }
    }

    private fun triggerShieldEffect(entity: Entity?, targetPosition: Position, abilityComponent: AbilityComponent) {
        // Should play an animation that the shield breaks.
        // If the shield was called through capturing, we actually do the cooldown.
        if (CapturedComponent.mapper.get(entity) != null) {
            entity?.add(BlockedComponent())
        } else {
            // Reset the cooldown so the shield stays active again.
            // Can be considered to remove
            abilityComponent.currentAbilityCDTime = 0
        }
    }
}
