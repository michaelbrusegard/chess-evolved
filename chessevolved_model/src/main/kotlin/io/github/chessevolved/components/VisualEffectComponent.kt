package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.graphics.Color

enum class VisualEffectType {
    SHIELD_ACTIVE,
    SHIELD_BREAK,
    EXPLOSION,
}

/**
 * @param effectType The visual effect type.
 * @param durationSeconds Duration in seconds. 0f is infinite.
 */
class VisualEffectComponent(
    val effectType: VisualEffectType,
    val durationSeconds: Float,
) : Component {
    companion object {
        val mapper : ComponentMapper<VisualEffectComponent> =
            ComponentMapper.getFor(VisualEffectComponent::class.java)
    }
}
