package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

class BoardSizeComponent(squareSize: Int) : Component {
    var boardSize: Int = squareSize
}
