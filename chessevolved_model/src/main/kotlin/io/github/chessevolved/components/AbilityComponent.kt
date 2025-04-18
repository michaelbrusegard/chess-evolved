package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

enum class AbilityType {
    SHIELD,
    EXPLOSION,
    SWAP,
    MIRROR,
    NEW_MOVEMENT,
}

class AbilityComponent(
    val ability: AbilityType,
    val abilityCooldownTime: Int,
    var currentAbilityCDTime: Int,
) : Component {
    companion object {
        val mapper: ComponentMapper<AbilityComponent> =
            ComponentMapper.getFor(AbilityComponent::class.java)
    }
}
