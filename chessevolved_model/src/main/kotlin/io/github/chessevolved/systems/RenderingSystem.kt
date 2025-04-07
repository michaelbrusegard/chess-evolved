package io.github.chessevolved.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent

class RenderingSystem(
    private val batch: SpriteBatch,
) : IteratingSystem(
        Family.all(PositionComponent::class.java, TextureRegionComponent::class.java).get(),
    ) {
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val textureMapper = ComponentMapper.getFor(TextureRegionComponent::class.java)

    override fun processEntity(
        entity: Entity,
        deltaTime: Float,
    ) {
        val position = positionMapper.get(entity)
        val texture = textureMapper.get(entity)

        if (position != null && texture != null) {
            texture.region?.let { region ->
                batch.draw(
                    region,
                    position.position.x.toFloat(),
                    position.position.y.toFloat(),
                    1f,
                    1f,
                )
            }
        }
    }
}
