package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

class CapturedComponent(
    val capturedByAbility: Boolean = false,
) : Component {
    companion object {
        val mapper: ComponentMapper<CapturedComponent> =
            ComponentMapper.getFor(CapturedComponent::class.java)
    }
}
