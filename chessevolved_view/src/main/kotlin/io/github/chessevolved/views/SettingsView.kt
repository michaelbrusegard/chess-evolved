package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.chessevolved.dtos.SettingsDto
import ktx.actors.onClick
import ktx.scene2d.checkBox
import ktx.scene2d.label
import ktx.scene2d.scene2d
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

    var onApplyClicked: (SettingsDto) -> Unit = {}
    var onCancelClicked: () -> Unit = {}

    override fun init() {
        stage =
            Stage(
                FitViewport(
                    Gdx.graphics.width.toFloat(),
                    Gdx.graphics.height.toFloat(),
                ),
            )
        toastManager = ToastManager(stage)

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f)

                label("Settings") { it.colspan(2).padBottom(50f).center() }
                row()

                label("Fog of War:") { it.right() }
                fogOfWarCheckBox = checkBox("") { it.left() }
                row()

                label("Board Size (8-16):") { it.right() }
                boardSizeField =
                    textField("8") {
                        it.width(60f).left()
                        maxLength = 2
                        alignment = Align.center
                        textFieldFilter =
                            TextField.TextFieldFilter.DigitsOnlyFilter()
                    }
                row()

                table {
                    defaults().pad(5f).width(150f)
                    textButton("Apply") {
                        onClick { applySettings() }
                    }
                    textButton("Cancel") { onClick { onCancelClicked() } }
                }.cell(colspan = 2, padTop = 50f)
            }

        stage.addActor(root)
    }

    private fun applySettings() {
        val boardSize = boardSizeField.text.toIntOrNull()
        val isFogOfWar = fogOfWarCheckBox.isChecked

        if (boardSize != null && boardSize in 8..16) {
            val settings = SettingsDto(fogOfWar = isFogOfWar, boardSize = boardSize)
            onApplyClicked(settings)
        } else {
            toastManager.showError("Board size must be a number between 8 and 16")
            boardSizeField.text = "8"
        }
    }

    fun setExistingSettings(settings: SettingsDto) {
        fowSetting = settings.fogOfWar
        fogOfWarCheckBox.isChecked = fowSetting

        if (settings.boardSize in 8..16) {
            boardSizeSetting = settings.boardSize
            boardSizeField.text = boardSizeSetting.toString()
        } else {
            throw IllegalStateException("Invalid board size in settings")
        }
    }

    fun setInitialValues(initialSettings: SettingsDto) {
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
