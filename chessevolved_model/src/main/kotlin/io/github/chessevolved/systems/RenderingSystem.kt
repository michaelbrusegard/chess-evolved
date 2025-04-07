package io.github.chessevolved.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import ktx.ashley.allOf
import ktx.ashley.get

class RenderingSystem(
    private val batch: SpriteBatch,
) : IteratingSystem(
        allOf(PositionComponent::class, TextureRegionComponent::class).get(),
    ) {
    private val positionMapper: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    private val textureMapper: ComponentMapper<TextureRegionComponent> = ComponentMapper.getFor(TextureRegionComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }

    override fun processEntity(
        entity: Entity,
        deltaTime: Float,
    ) {
        positionMapper.get(entity)?.let { position ->
            textureMapper.get(entity)?.let { texture ->
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
}
