package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

class BlockedComponent : Component {
    companion object {
        val mapper: ComponentMapper<BlockedComponent> =
            ComponentMapper.getFor(BlockedComponent::class.java)
    }
}
