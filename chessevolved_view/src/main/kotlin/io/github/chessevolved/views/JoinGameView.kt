package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextField
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
import ktx.scene2d.textField

class JoinGameView : IView {
    private lateinit var stage: Stage
    private lateinit var inputField: TextField
    private lateinit var toastManager: ToastManager

    var onJoinButtonClicked: (String) -> Unit = {}
    var onReturnButtonClicked: () -> Unit = {}

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

        val tiledDrawable = TiledDrawable(
            TextureRegion(
            backgroundTexture
        )
        )
        val background = Image(tiledDrawable).apply {
            setSize(1500f, 1500f)
            setPosition(-10f, -10f)
            color = Color(1f, 1f, 1f, 0.25f)
            zIndex = 0
        }

        stage.addActor(background)

        toastManager = ToastManager(stage)

        val logoTexture = skinSetup.assetManager.get("customUI/chessEvolvedLogo.png", Texture::class.java)
        val labelPanelTexture = skinSetup.assetManager.get("customUI/labelPanel.png", Texture::class.java)

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

                stack { stackTable ->
                    val ratio = labelPanelTexture.width / labelPanelTexture.height

                    image(labelPanelTexture) {
                        setSize(300f, 300f / ratio)
                    }

                    label("ENTER LOBBY CODE", style = "CElabelStyle") {
                        setFontScale(0.5f)
                        color = Color.BLACK
                        setAlignment(Align.center)
                    }

                    stackTable.size(300f, 300f / ratio)
                    stackTable.padBottom(-5f)
                }
                row()

                inputField =
                    textField(style = "CEtextFieldStyle") {
                        it.size(200f, 66f)

                        alignment = Align.center

                        maxLength = 5
                        messageText = "A1B2C"

                        textFieldFilter =
                            TextField.TextFieldFilter { _, c ->
                                c.isLetterOrDigit()
                            }
                        setTextFieldListener { field, _ ->
                            val cursor = field.cursorPosition
                            field.text = field.text.uppercase()
                            field.setCursorPosition(cursor)
                        }
                    }
                row()

                textButton("JOIN GAME", style = "CEtextButtonStyle") {
                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(300f, 300f / ratio)
                    label.setFontScale(0.75f)
                    label.color = Color.BLACK

                    onClick {
                        val code = inputField.text.trim()
                        if (code.length == 5) {
                            onJoinButtonClicked(code)
                        } else {
                            toastManager.showError(
                                "Lobby code must be 5 characters",
                            )
                        }
                    }
                }
                row()

                textButton("RETURN TO\nMENU", style = "CEtextButtonStyle") {
                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(300f, 300f / ratio)
                    label.setFontScale(0.75f)
                    label.color = Color.BLACK
                    onClick { onReturnButtonClicked() }
                }
            }

        stage.addActor(root)
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

    fun showJoinSuccess() {
        toastManager.showSuccess("Successfully joined the lobby!")
    }

    fun showJoinError(message: String) {
        toastManager.showError("Error: $message")
    }
}
