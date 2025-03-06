package io.github.chess2.components

import com.badlogic.ashley.core.Component

class BoardSizeComponent(squareSize: Int) : Component {
    var boardSize: Int = squareSize
}
