package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

class PositionComponent(x: Int, y: Int) : Component{
    var xPos: Int = x
    var yPos: Int = y

    fun changePosition(x: Int, y: Int): Unit {
        xPos = x
        yPos = y
    }
}
