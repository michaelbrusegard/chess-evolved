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
import ktx.graphics.color
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.stack
import ktx.scene2d.table
import ktx.scene2d.textButton
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class LobbyView(
    private val lobbyCode: String,
) : IView {
    private lateinit var stage: Stage
    private lateinit var secondPlayerStatusLabel: Label
    private lateinit var startGameButton: TextButton
    private lateinit var toastManager: ToastManager

    var onLeaveButtonClicked: () -> Unit = {}
    var onOpenSettingsButtonClicked: () -> Unit = {}
    var onStartGameButtonClicked: () -> Unit = {}

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

                table {
                    stack { stackTable ->
                        val ratio = labelPanelTexture.width / labelPanelTexture.height

                        image(labelPanelTexture) {
                            setSize(300f, 300f / ratio)
                        }

                        label("LOBBY CODE: $lobbyCode", style = "CElabelStyle") {
                            setFontScale(0.5f)
                            color = Color.BLACK
                            setAlignment(Align.center)
                        }

                        stackTable.size(300f, 300f / ratio)
                    }

                    imageButton(style = "CEcopyButtonStyle") {
                        it.padLeft(20f)
                        it.size(90f, 90f)
                        onClick { copyLobbyCode() }
                    }
                }
                row()
                stack { stackTable ->
                    image(labelPanelTexture) {
                        setSize(400f, 75f)
                    }

                    secondPlayerStatusLabel =
                        label("WAITING FOR SECOND PLAYER...", style = "CElabelStyle") {
                            setFontScale(0.45f)
                            color = Color.BLACK
                            setAlignment(Align.center)
                        }

                    stackTable.size(400f, 75f)
                    stackTable.padBottom(10f)
                }
                row()

                startGameButton =
                    textButton("START GAME", style = "CEtextButtonStyle") {
                        it.padBottom(5f)

                        val ratio = style.up.minWidth / style.up.minHeight

                        it.size(250f, 250f / ratio)
                        label.setFontScale(0.65f)
                        label.color = Color.BLACK
                    }
                row()

                textButton("LOBBY\nSETTINGS", style = "CEtextButtonStyle") {
                    it.padBottom(5f)

                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(250f, 250f / ratio)
                    label.setFontScale(0.65f)
                    label.color = Color.BLACK

                    onClick { onOpenSettingsButtonClicked() }
                }
                row()

                textButton("LEAVE\nLOBBY", style = "CEtextButtonStyle") {
                    it.padBottom(5f)

                    val ratio = style.up.minWidth / style.up.minHeight

                    it.size(250f, 250f / ratio)
                    label.setFontScale(0.65f)
                    label.color = Color.BLACK

                    onClick { onLeaveButtonClicked() }
                }
            }

        stage.addActor(root)
    }

    fun setSecondPlayerConnected(isConnected: Boolean) {
        if (isConnected) {
            secondPlayerStatusLabel.setText("SECOND PLAYER CONNECTED!")
            startGameButton.isDisabled = false
            startGameButton.clearListeners()
            startGameButton.onClick { onStartGameButtonClicked() }
            startGameButton.color = Color.WHITE
        } else {
            secondPlayerStatusLabel.setText("WAITING FOR SECOND PLAYER...")
            startGameButton.isDisabled = true
            startGameButton.clearListeners()
            startGameButton.setColor(0.2f, 0.2f, 0.2f, 1f)
        }
    }

    private fun copyLobbyCode() {
        val selection = StringSelection(lobbyCode)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
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

    fun showError(message: String) {
        toastManager.showError(message)
    }
}
