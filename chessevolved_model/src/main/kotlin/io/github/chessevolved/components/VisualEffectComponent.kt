package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

enum class VisualEffectType(val value: Float) {
    SHIELD_ACTIVE(0.5f),
    SHIELD_BREAK(0.1f),
    EXPLOSION(0.1f),
}

enum class VisualEffectSize(val value: Int) {
    NORMAL(1), // 1x1
    MEDIUM(3), // 3x3
    LARGE(5), // 5x5
}

/**
 * @param effectType The visual effect type.
 * @param amountOfFrames The amount of frames for this visual effect
 * @param frameDuration Amount of time each frame should take.
 * @param duration Duration based on frames. 0 if infinite
 * @param squareSize How many squares it should be sized to. 1 square is 1, 2 squares is 4 (2x2).
 */
class VisualEffectComponent(
    val effectType: VisualEffectType,
    val amountOfFrames: Int,
    val duration: Float = amountOfFrames * effectType.value,
    val squareSize: VisualEffectSize,
) : Component {
    companion object {
        val mapper: ComponentMapper<VisualEffectComponent> =
            ComponentMapper.getFor(VisualEffectComponent::class.java)
    }
}
