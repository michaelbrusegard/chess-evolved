package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.AbilityCardComponent
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityType
import io.github.chessevolved.components.TextureRegionComponent

class AbilityItemFactory(
    private val engine: Engine,
    private val assetManager: AssetManager,
) {
    private fun getAbilityItemTexture(abilityType: AbilityType): TextureRegion =
        when (abilityType) {
            AbilityType.SHIELD -> TextureRegion(assetManager.get("abilities/shield-card.png", Texture::class.java))
            AbilityType.EXPLOSION -> TextureRegion(assetManager.get("abilities/explosion-card.png", Texture::class.java))
            AbilityType.SWAP -> TextureRegion(assetManager.get("abilities/swap-card.png", Texture::class.java))
            AbilityType.MIRROR -> TextureRegion(assetManager.get("abilities/mirror-card.png", Texture::class.java))
            AbilityType.NEW_MOVEMENT -> TextureRegion(assetManager.get("abilities/new_movement-card.png", Texture::class.java))
        }

    fun createAbilityItem(abilityType: AbilityType): Entity =
        Entity().apply {
            add(AbilityComponent(listOf(abilityType)))
            add(AbilityCardComponent())
            add(TextureRegionComponent(getAbilityItemTexture(abilityType)))
            engine.addEntity(this)
        }
}
