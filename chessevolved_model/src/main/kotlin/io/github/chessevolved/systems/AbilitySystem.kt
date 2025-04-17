package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityTriggerComponent
import io.github.chessevolved.components.AbilityType
import io.github.chessevolved.components.Position

class AbilitySystem : IteratingSystem(
    Family.all(AbilityComponent::class.java, AbilityTriggerComponent::class.java).get()
)
{
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val abilityComponent = AbilityComponent.mapper.get(entity)
        val abilityTriggerComponent = AbilityTriggerComponent.mapper.get(entity)

        when (abilityComponent.ability) {
            AbilityType.EXPLOSION -> {}
            AbilityType.SWAP -> {}
            AbilityType.SHIELD -> {}
            AbilityType.MIRROR -> {}
            AbilityType.NEW_MOVEMENT -> {}
        }

        entity?.remove(AbilityTriggerComponent::class.java)
    }

    private fun triggerExplosionEffect(entity: Entity?, targetPosition: Position) {
        // From target position.
        // Hard coded values define the Explosion
        // Search from here all the pieces on positions in the radius.
        // Add an animation component which either the rendering system or an own system handles.
    }
}
