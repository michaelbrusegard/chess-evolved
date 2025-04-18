package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.VisualEffectComponent
import io.github.chessevolved.components.VisualEffectType
import io.github.chessevolved.singletons.ECSEngine

class VisualEffectSystem(
    private val batch: SpriteBatch,
    private val assetManager: AssetManager
) : IteratingSystem(
    Family.all(
        VisualEffectComponent::class.java,
        HighlightComponent::class.java,
        PositionComponent::class.java).get()
)
{
    private val filePathPrefix = "ability/effects/"
    private val entityTimers = mutableMapOf<Entity, Float>()
    private val animations = mutableMapOf<VisualEffectType, Animation<TextureRegion>>()

    init {
        // Load explosion frames
        for (i in 1..6) {
            assetManager.load(filePathPrefix + "explosion/explosion$i.png", Texture::class.java)
        }
    }

    fun initializeAnimations() {
        val explosionFrames = com.badlogic.gdx.utils.Array<TextureRegion>()
        for (i in 1..6) {
            explosionFrames.add(TextureRegion(assetManager.get(filePathPrefix + "explosion/explosion$i.png", Texture::class.java)))
        }
        animations[VisualEffectType.EXPLOSION] = Animation(0.1f, explosionFrames, Animation.PlayMode.NORMAL)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        entityTimers.forEach { (entity, elapsedTime) ->
            val visualEffect = VisualEffectComponent.mapper.get(entity)
            val newElapsedTime = elapsedTime + deltaTime

            if (visualEffect.durationSeconds > 0 && newElapsedTime >= visualEffect.durationSeconds) {
                entityTimers.remove(entity)
                ECSEngine.removeEntity(entity)
            } else {
                entityTimers[entity] = newElapsedTime
            }
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (entity == null) {
            return
        }

        val positionComponent = PositionComponent.mapper.get(entity)
        val highlightComponent = HighlightComponent.mapper.get(entity)
        val visualEffectComponent = VisualEffectComponent.mapper.get(entity)

        if (!entityTimers.containsKey(entity)) {
            entityTimers[entity] = 0f
        }

        val elapsedTime = entityTimers[entity] ?: 0f

        val animation = animations[visualEffectComponent.effectType]

        if (animation != null) {
            val currentFrame = animation.getKeyFrame(elapsedTime)

            batch.color = highlightComponent.color
            batch.draw(
                currentFrame,
                positionComponent.position.x.toFloat(),
                positionComponent.position.y.toFloat(),
                1f,
                1f
            )

            batch.color = Color.WHITE
        }
    }
}
