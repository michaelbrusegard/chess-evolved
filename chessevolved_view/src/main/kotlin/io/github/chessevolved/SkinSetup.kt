package io.github.chessevolved

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.scene2d.Scene2DSkin
import ktx.style.get
import ktx.style.has

object SkinSetup {
    var assetManager = AssetManager()
        private set

    init {
        loadAllCommonAssets()

        setupSkins()
    }

    fun setupSkins() {
        if (!Scene2DSkin.defaultSkin.has("CEtextButtonStyle", TextButtonStyle::class.java) ) {
            val textButtonStyle = TextButtonStyle().apply {
                up = TextureRegionDrawable(
                    TextureRegion(
                        assetManager.get("customUI/buttonNormal.png", Texture::class.java)
                    )
                )
                down = TextureRegionDrawable(
                    TextureRegion(
                        assetManager.get("customUI/buttonPressed.png", Texture::class.java)
                    )
                )
                over = TextureRegionDrawable(
                    TextureRegion(
                        assetManager.get("customUI/buttonNormal.png", Texture::class.java)
                    )
                )
                font = assetManager.get("customUI/pixeled.fnt", BitmapFont::class.java)

                font.data.setLineHeight(0.5f * font.data.lineHeight)
            }

            Scene2DSkin.defaultSkin.add("CEtextButtonStyle", textButtonStyle)
        }
    }

    fun loadAllCommonAssets() {
        if (!assetManager.isLoaded("customUI/buttonNormal.png")) {
            assetManager.load("customUI/buttonNormal.png", Texture::class.java)
        }
        if (!assetManager.isLoaded("customUI/buttonPressed.png")) {
            assetManager.load("customUI/buttonPressed.png", Texture::class.java)
        }
        if (!assetManager.isLoaded("customUI/buttonPressed.png")) {
            assetManager.load("customUI/buttonPressed.png", Texture::class.java)
        }
        if (!assetManager.isLoaded("customUI/backgroundTile.png")) {
            assetManager.load("customUI/backgroundTile.png", Texture::class.java)
        }
        if (!assetManager.isLoaded("customUI/pixeled.fnt")) {
            assetManager.load("customUI/pixeled.fnt", BitmapFont::class.java)
        }

        assetManager.finishLoading()
    }

    fun unloadAllCommonAssets() {
        if (assetManager.isLoaded("customUI/buttonNormal.png")) {
            assetManager.unload("customUI/buttonNormal.png")
        }
        if (assetManager.isLoaded("customUI/buttonPressed.png")) {
            assetManager.unload("customUI/buttonPressed.png")
        }
        if (assetManager.isLoaded("customUI/buttonPressed.png")) {
            assetManager.unload("customUI/buttonPressed.png")
        }
        if (assetManager.isLoaded("customUI/backgroundTile.png")) {
            assetManager.unload("customUI/backgroundTile.png")
        }
        if (assetManager.isLoaded("customUI/pixeled.fnt")) {
            assetManager.unload("customUI/pixeled.fnt")
        }
    }
}
