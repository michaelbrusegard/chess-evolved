package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.chessevolved.SkinSetup
import io.github.chessevolved.shared.SettingsDTO
import ktx.actors.onClick
import ktx.scene2d.checkBox
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.stack
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.textField
import kotlin.text.toIntOrNull

class SettingsView : IView {
    private lateinit var stage: Stage
    private lateinit var fogOfWarCheckBox: CheckBox
    private lateinit var boardSizeField: TextField
    private lateinit var toastManager: ToastManager

    private var boardSizeSetting = 8
    private var fowSetting = false

    var onApplyClicked: (SettingsDTO) -> Unit = {}
    var onCancelClicked: () -> Unit = {}

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

        val labelPanelTexture = skinSetup.assetManager.get("customUI/labelPanel.png", Texture::class.java)

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f)

                stack { stackTable ->
                    stackTable.colspan(2)
                    val ratio = labelPanelTexture.width / labelPanelTexture.height

                    image(labelPanelTexture) {
                        setSize(300f, 300f / ratio)
                    }

                    label("SETTINGS", style = "CElabelStyle") {
                        setFontScale(0.7f)
                        color = Color.BLACK
                        setAlignment(Align.center)
                    }

                    stackTable.size(300f, 300f / ratio)
                    stackTable.padBottom(50f)
                }
                row()

                stack { stackTable ->
                    val ratio = labelPanelTexture.width / labelPanelTexture.height

                    image(labelPanelTexture) {
                        setSize(250f, 250f / ratio)
                    }

                    label("FOG OF WAR", style = "CElabelStyle") {
                        setFontScale(0.5f)
                        color = Color.BLACK
                        setAlignment(Align.center)
                    }

                    stackTable.size(250f, 250f / ratio)
                }
                fogOfWarCheckBox = checkBox("", style = "CEcheckboxStyle") {
                    it.left()
                }
                row()

                stack { stackTable ->
                    val ratio = labelPanelTexture.width / labelPanelTexture.height

                    image(labelPanelTexture) {
                        setSize(250f, 250f / ratio)
                    }

                    label("BOARD SIZE (8-16)", style = "CElabelStyle") {
                        setFontScale(0.5f)
                        color = Color.BLACK
                        setAlignment(Align.center)
                    }

                    stackTable.size(250f, 250f / ratio)
                }
                boardSizeField =
                    textField("8", style = "CEtextFieldStyle") {
                        it.size(65f, 65f)
                        it.left()
                        maxLength = 2
                        alignment = Align.center
                        textFieldFilter =
                            TextField.TextFieldFilter.DigitsOnlyFilter()
                    }
                row()

                table {
                    defaults().pad(5f).width(150f)
                    textButton("APPLY", style = "CEtextButtonStyle") {
                        val ratio = style.up.minWidth / style.up.minHeight

                        it.size(200f, 200f / ratio)
                        label.setFontScale(0.75f)
                        label.color = Color.BLACK

                        onClick { applySettings() }
                    }
                    textButton("CANCEL", style = "CEtextButtonStyle") {
                        val ratio = style.up.minWidth / style.up.minHeight

                        it.size(200f, 200f / ratio)
                        label.setFontScale(0.75f)
                        label.color = Color.BLACK

                        onClick { onCancelClicked() }
                    }
                }.cell(colspan = 2, padTop = 50f)
            }

        stage.addActor(root)
        toastManager = ToastManager(stage)
    }

    private fun applySettings() {
        val boardSize = boardSizeField.text.toIntOrNull()
        val isFogOfWar = fogOfWarCheckBox.isChecked

        if (boardSize != null && boardSize in 8..16) {
            val settings = SettingsDTO(fogOfWar = isFogOfWar, boardSize = boardSize)
            onApplyClicked(settings)
        } else {
            toastManager.showError("Board size must be a number between 8 and 16")
            boardSizeField.text = "8"
        }
    }

    fun setExistingSettings(settings: SettingsDTO) {
        fowSetting = settings.fogOfWar
        fogOfWarCheckBox.isChecked = fowSetting

        if (settings.boardSize in 8..16) {
            boardSizeSetting = settings.boardSize
            boardSizeField.text = boardSizeSetting.toString()
        } else {
            throw IllegalStateException("Invalid board size in settings")
        }
    }

    fun setInitialValues(initialSettings: SettingsDTO) {
        if (::fogOfWarCheckBox.isInitialized) {
            fogOfWarCheckBox.isChecked = initialSettings.fogOfWar
        }
        if (::boardSizeField.isInitialized) {
            boardSizeField.text = initialSettings.boardSize.toString()
        }
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
}
