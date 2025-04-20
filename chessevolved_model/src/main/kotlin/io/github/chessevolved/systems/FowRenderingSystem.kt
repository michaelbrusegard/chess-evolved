package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.components.FowComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.singletons.GameSettings

class FowRenderingSystem(
    private val batch: SpriteBatch,
    private val texture: Texture,
    private val isFowEnabled: Boolean,
) : IteratingSystem(
        Family.all(PositionComponent::class.java, FowComponent::class.java).get(),
    ) {
    override fun processEntity(
        entity: Entity,
        deltaTime: Float,
    ) {
        if (!isFowEnabled) return

        val position = PositionComponent.mapper.get(entity)
        val fow = FowComponent.mapper.get(entity)

        if (position != null && fow != null) {
            if (fow.showFog && (position.position.y >= (GameSettings.getBoardSize() + 1) / 2)) {
                batch.draw(
                    texture,
                    position.position.x.toFloat(),
                    position.position.y.toFloat(),
                    1f,
                    1f,
                )
            }
        }
    }
}
