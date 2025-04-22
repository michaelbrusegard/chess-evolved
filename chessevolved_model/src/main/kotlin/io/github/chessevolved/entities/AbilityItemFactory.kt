package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.AbilityCardComponent
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.enums.AbilityType

class AbilityItemFactory(
    private val engine: Engine,
    private val assetManager: AssetManager,
) {
    private fun getAbilityItemTexture(abilityType: AbilityType): TextureRegion =
        when (abilityType) {
            AbilityType.SHIELD -> TextureRegion(assetManager.get("abilities/cards/shieldCard.png", Texture::class.java))
            AbilityType.EXPLOSION -> TextureRegion(assetManager.get("abilities/cards/explosionCard.png", Texture::class.java))
            AbilityType.SWAP -> TextureRegion(assetManager.get("abilities/cards/swapCard.png", Texture::class.java))
            AbilityType.MIRROR -> TextureRegion(assetManager.get("abilities/cards/mirrorCard.png", Texture::class.java))
            AbilityType.NEW_MOVEMENT -> TextureRegion(assetManager.get("abilities/cards/new_movementCard.png", Texture::class.java))
        }

    fun createAbilityItem(abilityType: AbilityType): Entity {
        return Entity().apply {
            add(AbilityComponent(abilityType, abilityType.cooldownTime, 0))
            add(AbilityCardComponent())
            add(TextureRegionComponent(getAbilityItemTexture(abilityType)))
            engine.addEntity(this)
        }
    }
}
