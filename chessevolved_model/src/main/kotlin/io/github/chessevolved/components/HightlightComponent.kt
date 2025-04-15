package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.graphics.Color

class HighlightComponent(val color: Color) : Component {
    companion object {
        val mapper: ComponentMapper<HighlightComponent> =
            ComponentMapper.getFor(HighlightComponent::class.java)
    }
}
