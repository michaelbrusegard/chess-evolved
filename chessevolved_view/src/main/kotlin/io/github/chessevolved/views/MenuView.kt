package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.image
import ktx.scene2d.label
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

    private val logoTexture = Texture("customUI/chessEvolvedLogo.png")

    private val bitmapFont = BitmapFont(Gdx.files.internal("customUI/pixeled.fnt"))

    override fun init() {
        setupSkins()

        val screenRatio = Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        viewport = FitViewport(500f, 500f / screenRatio, camera)

        stage = Stage(viewport)

        println("ScreenWidth: ${Gdx.graphics.width}, ScreenHeight: ${Gdx.graphics.height}")

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
            }

        toastManager = ToastManager(stage)
        stage.addActor(root)
    }

    private fun setupSkins() {
        val textButtonStyle = TextButtonStyle().apply {
            up = TextureRegionDrawable(TextureRegion(Texture("customUI/buttonNormal.png")))
            down = TextureRegionDrawable(TextureRegion(Texture("customUI/buttonPressed.png")))
            over = TextureRegionDrawable(TextureRegion(Texture("customUI/buttonNormal.png")))
            font = bitmapFont

            font.data.setLineHeight(0.5f * font.data.lineHeight)
        }

        Scene2DSkin.defaultSkin.add("CEtextButtonStyle", textButtonStyle)
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
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = stage
    }

    fun showCreateGameError(message: String) {
        toastManager.showError("Error: $message")
    }
}
