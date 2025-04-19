package io.github.chessevolved

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.scene2d.Scene2DSkin

object SkinSetup {
    var assetManager = AssetManager()
        private set

    init {
        loadAllCommonAssets()

        setupSkins()
    }

    fun setupSkins() {
        if (!Scene2DSkin.defaultSkin.has("CEtextButtonStyle", TextButtonStyle::class.java)) {
            val textButtonStyle = TextButtonStyle().apply {
                up = TextureRegionDrawable(TextureRegion(
                        assetManager.get("customUI/buttonNormal.png", Texture::class.java)
                ))
                down = TextureRegionDrawable(TextureRegion(
                        assetManager.get("customUI/buttonPressed.png", Texture::class.java)
                ))
                over = TextureRegionDrawable(TextureRegion(
                        assetManager.get("customUI/buttonNormal.png", Texture::class.java)
                ))

                font = assetManager.get("customUI/pixeled.fnt", BitmapFont::class.java)


                font.data.setLineHeight(0.5f * font.data.lineHeight)
            }

            Scene2DSkin.defaultSkin.add("CEtextButtonStyle", textButtonStyle)
        }

        if (!Scene2DSkin.defaultSkin.has("CEcopyButtonStyle", ImageButtonStyle::class.java)) {
            val imageButtonStyle = ImageButtonStyle().apply {
                up = TextureRegionDrawable(TextureRegion(
                    assetManager.get("customUI/copySymbol.png", Texture::class.java)
                ))
                down = TextureRegionDrawable(TextureRegion(
                    assetManager.get("customUI/copySymbolPressed.png", Texture::class.java)
                ))
            }

            Scene2DSkin.defaultSkin.add("CEcopyButtonStyle", imageButtonStyle)
        }

        if (!Scene2DSkin.defaultSkin.has("CElabelStyle", LabelStyle::class.java)) {
            val labelStyle = LabelStyle().apply {
                font = assetManager.get("customUI/pixeled.fnt", BitmapFont::class.java)
            }

            Scene2DSkin.defaultSkin.add("CElabelStyle", labelStyle)
        }
    }

    fun loadAllCommonAssets() {
        assetManager.load("customUI/chessEvolvedLogo.png", Texture::class.java)
        assetManager.load("customUI/buttonNormal.png", Texture::class.java)
        assetManager.load("customUI/buttonPressed.png", Texture::class.java)
        assetManager.load("customUI/buttonPressed.png", Texture::class.java)
        assetManager.load("customUI/backgroundTile.png", Texture::class.java)
        assetManager.load("customUI/pixeled.fnt", BitmapFont::class.java)
        assetManager.load("customUI/copySymbol.png", Texture::class.java)
        assetManager.load("customUI/copySymbolPressed.png", Texture::class.java)
        assetManager.load("customUI/labelPanel.png", Texture::class.java)

        assetManager.finishLoading()
    }

    fun unloadAllCommonAssets() {
        assetManager.unload("customUI/chessEvolvedLogo.png")
        assetManager.unload("customUI/buttonNormal.png")
        assetManager.unload("customUI/buttonPressed.png")
        assetManager.unload("customUI/buttonPressed.png")
        assetManager.unload("customUI/backgroundTile.png")
        assetManager.unload("customUI/pixeled.fnt")
        assetManager.unload("customUI/copySymbol.png")
        assetManager.unload("customUI/copySymbolPressed.png")
        assetManager.unload("customUI/labelPanel.png")
    }
}
