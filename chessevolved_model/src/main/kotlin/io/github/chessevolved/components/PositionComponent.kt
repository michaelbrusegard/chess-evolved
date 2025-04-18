package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import kotlinx.serialization.Serializable

@Serializable
data class Position(
    val x: Int,
    val y: Int,
)

class PositionComponent(
    position: Position = Position(0, 0),
) : Component {
    companion object {
        val mapper: ComponentMapper<PositionComponent> =
            ComponentMapper.getFor(PositionComponent::class.java)
    }

    var position: Position = position

    fun changePosition(newPosition: Position) {
        position = newPosition
    }
}
