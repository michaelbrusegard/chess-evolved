package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityType
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.TextureRegionComponent

class AbilityItemFactory(
    private val engine: Engine,
    private val assetManager: AssetManager,
) {
    fun getAbilityItemTexture(abilityType: AbilityType): TextureRegion =
        when (abilityType) {
            AbilityType.SHIELD -> TextureRegion(assetManager.get("icons/copy-icon.png", Texture::class.java))
            AbilityType.EXPLOSION -> TextureRegion(assetManager.get("icons/copy-icon.png", Texture::class.java))
            AbilityType.SWAP -> TextureRegion(assetManager.get("icons/copy-icon.png", Texture::class.java))
            AbilityType.MIRROR -> TextureRegion(assetManager.get("icons/copy-icon.png", Texture::class.java))
            AbilityType.NEW_MOVEMENT -> TextureRegion(assetManager.get("icons/copy-icon.png", Texture::class.java))
        }

    fun createAbilityItem(abilityType: AbilityType): Entity =
        Entity().apply {
            add(AbilityComponent(listOf(abilityType)))
            add(TextureRegionComponent(getAbilityItemTexture(abilityType)))
            add(HighlightComponent(Color.BLACK))

            engine.addEntity(this)
        }
}
