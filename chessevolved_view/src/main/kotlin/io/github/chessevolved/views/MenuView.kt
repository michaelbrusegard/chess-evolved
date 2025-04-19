package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.chessevolved.SkinSetup
import ktx.actors.onClick
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

class MenuView : IView {
    private lateinit var stage: Stage

    var onCreateLobbyButtonClicked: () -> Unit = {}
    var onJoinGameButtonClicked: () -> Unit = {}

    private lateinit var toastManager: ToastManager

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport

    private val skinSetup = SkinSetup

    override fun init() {
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

        val logoTexture = skinSetup.assetManager.get("customUI/chessEvolvedLogo.png", Texture::class.java)

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f).center()

                image(logoTexture) {
                    it.padBottom(20f)

                    val ratio = logoTexture.width.toFloat() / logoTexture.height.toFloat()
                    it.size(420f, 420f / ratio)
                }
                row()

                textButton("CREATE\nLOBBY", style = "CEtextButtonStyle") {
                    it.padBottom(5f)

                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(300f, 300f / ratio)
                    label.setFontScale(0.75f)
                    label.color = Color.BLACK
                    onClick { onCreateLobbyButtonClicked() }
                }
                row()

                textButton("JOIN GAME", style = "CEtextButtonStyle") {
                    it.padBottom(5f)

                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(300f, 300f / ratio)
                    label.setFontScale(0.75f)
                    label.color = Color.BLACK
                    onClick { onJoinGameButtonClicked() }
                }
                row()
                textButton("EXIT", style = "CEtextButtonStyle") {
                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(300f, 300f / ratio)
                    label.setFontScale(0.75f)
                    label.color = Color.BLACK
                    onClick { Gdx.app.exit() }
                }

                zIndex = 5
            }

        stage.addActor(root)
        toastManager = ToastManager(stage)
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
        skinSetup.unloadAllCommonAssets()
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = stage
    }

    fun showCreateGameError(message: String) {
        toastManager.showError("Error: $message")
    }
}
