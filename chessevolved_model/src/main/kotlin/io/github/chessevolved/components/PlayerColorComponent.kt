package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

enum class PlayerColor {
    BLACK,
    WHITE,
}

class PlayerColorComponent(
    var color: PlayerColor = PlayerColor.BLACK,
) : Component {
    companion object {
        val mapper: ComponentMapper<PlayerColorComponent> =
            ComponentMapper.getFor(PlayerColorComponent::class.java)
    }
}
