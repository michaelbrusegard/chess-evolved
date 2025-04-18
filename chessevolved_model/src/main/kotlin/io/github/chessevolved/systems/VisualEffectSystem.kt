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
    private val assetManager: AssetManager,
) : IteratingSystem(
        Family.all(
            VisualEffectComponent::class.java,
            HighlightComponent::class.java,
            PositionComponent::class.java,
        ).get(),
    ) {
    private val filePathPrefix = "ability/effects/"
    private val entityTimers = mutableMapOf<Entity, Float>()
    private val animations = mutableMapOf<VisualEffectType, Animation<TextureRegion>>()

    init {
        // Load explosion frames
        for (i in 1..6) {
            assetManager.load(filePathPrefix + "explosion/explosion$i.png", Texture::class.java)
            // Shield break is also 6 frames, so load this.
            assetManager.load(filePathPrefix + "shield/shieldBreak$i.png", Texture::class.java)
        }

        for (i in 1..3) {
            assetManager.load(filePathPrefix + "shield/shield$i.png", Texture::class.java)
        }
    }

    fun initializeAnimations() {
        addAnimation(6, VisualEffectType.EXPLOSION, Animation.PlayMode.NORMAL, "explosion/explosion")
        addAnimation(3, VisualEffectType.SHIELD_ACTIVE, Animation.PlayMode.LOOP, "shield/shield")
        addAnimation(6, VisualEffectType.SHIELD_BREAK, Animation.PlayMode.NORMAL, "shield/shieldBreak")
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val entitiesToRemove = mutableListOf<Entity>()

        entityTimers.forEach { (entity, elapsedTime) ->
            val visualEffect = VisualEffectComponent.mapper.get(entity)
            val newElapsedTime = elapsedTime + deltaTime

            if (visualEffect.duration > 0 && newElapsedTime >= visualEffect.duration) {
                entitiesToRemove.add(entity)
            } else {
                entityTimers[entity] = newElapsedTime
            }
        }

        entitiesToRemove.forEach { entity ->
            entityTimers.remove(entity)
            ECSEngine.removeEntity(entity)
        }
    }

    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
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

            val x = positionComponent.position.x - visualEffectComponent.squareSize.value / 2
            val y = positionComponent.position.y - visualEffectComponent.squareSize.value / 2

            batch.color = highlightComponent.color
            batch.draw(
                currentFrame,
                x.toFloat(),
                y.toFloat(),
                visualEffectComponent.squareSize.value.toFloat(),
                visualEffectComponent.squareSize.value.toFloat(),
            )

            batch.color = Color.WHITE
        }
    }

    private fun addAnimation(
        frameAmount: Int,
        effectType: VisualEffectType,
        animationPlayMode: Animation.PlayMode,
        filePathSuffix: String,
    ) {
        val frames = com.badlogic.gdx.utils.Array<TextureRegion>()
        for (i in 1..frameAmount) {
            frames.add(TextureRegion(assetManager.get("$filePathPrefix$filePathSuffix$i.png", Texture::class.java)))
        }
        animations[effectType] = Animation(effectType.value, frames, animationPlayMode)
    }
}
