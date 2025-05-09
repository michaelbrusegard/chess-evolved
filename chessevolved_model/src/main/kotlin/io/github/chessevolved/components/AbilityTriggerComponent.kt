package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.data.Position

class AbilityTriggerComponent(
    // The position of where the trigger happens
    val targetPosition: Position,
    val oldPosition: Position,
    val isActive: Boolean,
) : Component {
    companion object {
        val mapper: ComponentMapper<AbilityTriggerComponent> =
            ComponentMapper.getFor(AbilityTriggerComponent::class.java)
    }
}
