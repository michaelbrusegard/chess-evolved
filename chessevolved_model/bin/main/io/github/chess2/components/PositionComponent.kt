package io.github.chess2.components

import com.badlogic.ashley.core.Component

class PositionComponent(x: Int, y: Int) : Component{
    var xPos: Int = x
    var yPos: Int = y
}
