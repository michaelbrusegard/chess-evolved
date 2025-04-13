package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.checkBox
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.textField
import kotlin.text.toIntOrNull
import kotlin.text.toInt

class SettingsView : IView {
    private lateinit var stage: Stage
    private lateinit var fogOfWarCheckBox: CheckBox
    private lateinit var boardSizeField: TextField
    private lateinit var toastManager: ToastManager

    private var boardSizeSetting = 8
    private var fowSetting = false

    var onApply: (Boolean, Int) -> Unit = { _, _ -> }

    fun setCurrentSettings(currentSettings: Map<String, String>) {
        gameSettings = currentSettings
    }

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
        val boardSizeText = boardSizeField.text
        val boardSize = boardSizeText.toIntOrNull()
        val isFogOfWar = fogOfWarCheckBox.isChecked

        if (boardSize != null && boardSize in 8..16) {
            onApplyClicked(isFogOfWar, boardSize)
        } else {
            toastManager.showError("Board size must be a number between 8 and 16")
            boardSizeField.text = "8"
        }
    }

    fun setExistingSettings(settings: Map<String, String>) {
        for((key, setting) in settings) {
            when (key) {
                "FogOfWar" -> {
                    fowSetting = setting.toBooleanStrictOrNull() ?: false
                    fogOfWarCheckBox.isChecked = fowSetting
                }
                "BoardSize" -> {
                    val size = setting.toIntOrNull()
                    if(size != null && size in 8..16) {
                        boardSizeSetting = size
                        boardSizeField.text = size.toString()
                    }
                    else {
                        throw IllegalStateException("Invalid board size in settings")
                    }
                }
            }
        }
    }

    fun setInitialValues(
        fowEnabled: Boolean,
        boardSize: Int,
    ) {
        if (::fogOfWarCheckBox.isInitialized) {
            fogOfWarCheckBox.isChecked = fowEnabled
        }
        if (::boardSizeField.isInitialized) {
            boardSizeField.text = boardSize.toString()
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
