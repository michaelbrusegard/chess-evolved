package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.enums.PlayerColor

class PlayerColorComponent(
    var color: PlayerColor = PlayerColor.BLACK,
) : Component {
    companion object {
        val mapper: ComponentMapper<PlayerColorComponent> =
            ComponentMapper.getFor(PlayerColorComponent::class.java)
    }
}
