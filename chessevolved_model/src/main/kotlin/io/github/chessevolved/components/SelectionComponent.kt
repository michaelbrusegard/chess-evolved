package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

class SelectionComponent : Component {
    companion object {
        val mapper: ComponentMapper<SelectionComponent> =
            ComponentMapper.getFor(SelectionComponent::class.java)
    }
}
