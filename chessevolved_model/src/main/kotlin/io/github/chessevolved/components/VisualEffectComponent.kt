package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.enums.VisualEffectSize
import io.github.chessevolved.enums.VisualEffectType

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
    var duration: Float = amountOfFrames * effectType.value,
    val squareSize: VisualEffectSize,
) : Component {
    companion object {
        val mapper: ComponentMapper<VisualEffectComponent> =
            ComponentMapper.getFor(VisualEffectComponent::class.java)
    }
}
