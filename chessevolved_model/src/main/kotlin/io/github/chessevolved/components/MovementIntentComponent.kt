package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.data.Position

class MovementIntentComponent(var targetPosition: Position) : Component {
    companion object {
        val mapper: ComponentMapper<MovementIntentComponent> =
            ComponentMapper.getFor(MovementIntentComponent::class.java)
    }
}
