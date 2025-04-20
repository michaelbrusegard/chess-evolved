package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.chessevolved.SkinSetup
import ktx.actors.onClick
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.stack
import ktx.scene2d.table
import ktx.scene2d.textButton

class EndGameView : IView {
    private lateinit var stage: Stage

    // private lateinit var loseImage: Image
    // private lateinit var winImage: Image
    private lateinit var rematchLabel: Label
    private lateinit var rematchButton: TextButton

    var endGameStatus: Boolean = false

    var onReturnToMenuClicked: () -> Unit = {}
    var onRematchClicked: () -> Unit = {}

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport

    private val skinSetup = SkinSetup

//    private lateinit var loseLogoTexture: Texture
//    private lateinit var winLogoTexture: Texture

    override fun init() {
        skinSetup.assetManager.load("customUI/youloseLogo.png", Texture::class.java)
        skinSetup.assetManager.load("customUI/youwinLogo.png", Texture::class.java)

        skinSetup.loadAllCommonAssets()
        skinSetup.setupSkins()

        val screenRatio = Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        viewport = FitViewport(500f, 500f / screenRatio, camera)

        stage = Stage(viewport)

        // Make background repeat it's tile
        val backgroundTexture = SkinSetup.assetManager.get("customUI/backgroundTile.png", Texture::class.java)
        backgroundTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat)

        val tiledDrawable =
            TiledDrawable(
                TextureRegion(
                    backgroundTexture,
                ),
            )
        val background =
            Image(tiledDrawable).apply {
                setSize(1500f, 1500f)
                setPosition(-10f, -10f)
                color = Color(1f, 1f, 1f, 0.25f)
                zIndex = 0
            }

        stage.addActor(background)

        var fileLoc = "customUI/youloseLogo.png"
        if (endGameStatus) {
            fileLoc = "customUI/youwinLogo.png"
        }

        val logoTexture = skinSetup.assetManager.get(fileLoc, Texture::class.java)
        val labelPanelTexture = skinSetup.assetManager.get("customUI/labelPanel.png", Texture::class.java)

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f).center()
                image(logoTexture) {
                    val ratio = logoTexture.width.toFloat() / logoTexture.height.toFloat()

                    it.size(400f, 400f / ratio)
                    it.padBottom(5f)
                }
                row()
                stack { stackTable ->
                    val ratio = labelPanelTexture.width / labelPanelTexture.height

                    image(labelPanelTexture) {
                        setSize(300f, 300f / ratio)
                    }

                    rematchLabel =
                        label("WAITING FOR\nREMATCH REQUEST...", style = "CElabelStyle") {
                            setFontScale(0.5f)
                            color = Color.BLACK
                            setAlignment(Align.center)
                        }

                    stackTable.size(300f, 300f / ratio)
                    stackTable.padBottom(-5f)
                }

                row()

                textButton("RETURN TO\nMENU", style = "CEtextButtonStyle") {
                    it.padBottom(5f)
                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(250f, 250f / ratio)
                    label.setFontScale(0.65f)
                    label.color = Color.BLACK

                    onClick { onReturnToMenuClicked() }
                }
                row()
                rematchButton =
                    textButton("REQUEST\nREMATCH", style = "CEtextButtonStyle") {
                        val ratio = style.up.minWidth / style.up.minHeight

                        it.size(250f, 250f / ratio)
                        label.setFontScale(0.65f)
                        label.color = Color.BLACK

                        onClick { onRematchClicked() }
                    }
            }

        stage.addActor(root)
    }

    fun disableRematchButton() {
        rematchButton.isDisabled = true
        rematchButton.clearListeners()
        rematchButton.setColor(0f, 0f, 0f, 0.3f)
    }

    fun updateRematchText(text: String) {
        rematchLabel.setText(text.uppercase())
    }

    override fun render() {
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skinSetup.assetManager.unload("customUI/youloseLogo.png")
        skinSetup.assetManager.unload("customUI/youwinLogo.png")
        skinSetup.unloadAllCommonAssets()
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = stage
    }
}
