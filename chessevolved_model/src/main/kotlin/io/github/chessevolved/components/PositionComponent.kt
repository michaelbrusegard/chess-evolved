package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

data class Position(
    val x: Int,
    val y: Int,
)

class PositionComponent(
    position: Position = Position(0, 0),
) : Component {
    var position: Position = position

    fun changePosition(newPosition: Position) {
        position = newPosition
    }
}
