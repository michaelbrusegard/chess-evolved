package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

enum class PlayerColor {
    BLACK,
    WHITE,
}

class PlayerColorComponent(
    var color: PlayerColor = PlayerColor.BLACK,
) : Component
