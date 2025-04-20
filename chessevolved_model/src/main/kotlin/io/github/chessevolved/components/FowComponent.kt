package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

class FowComponent(
    var showFog: Boolean,
) : Component {
    companion object {
        val mapper: ComponentMapper<FowComponent> =
            ComponentMapper.getFor(FowComponent::class.java)
    }
}
