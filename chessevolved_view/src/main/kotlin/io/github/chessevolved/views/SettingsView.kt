import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.chessevolved.views.IView
import io.github.chessevolved.views.ToastManager
import ktx.actors.onClick
import ktx.scene2d.checkBox
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.textField

class SettingsView : IView {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private lateinit var fowField: Table
    private lateinit var fogOfWarCheckBox: CheckBox
    private lateinit var boardField: Table
    private lateinit var boardSizefield: TextField
    private lateinit var toastManager: ToastManager

    private var gameSettings: Map<String, String> = emptyMap()

    var onApply: (Boolean, Int) -> Unit = { _, _ -> }

    fun setCurrentSettings(currentSettings: Map<String, String>) {
        gameSettings = currentSettings
    }

    override fun init() {
        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f)

                label("Settings", "title") {
                    it.padBottom(100f)
                }
                row()

                fowField =
                    scene2d.table {
                        label("Fog of War") {
                            it.padRight(10f)
                        }
                        fogOfWarCheckBox =
                            checkBox("") {
                                onClick {
                                    println("Fog of War enabled: ${fogOfWarCheckBox.isChecked}")
                                }
                            }
                    }
                add(fowField)
                    .pad(10f)
                row()

                boardField =
                    scene2d.table {
                        label("Board size") {
                            it.padRight(10f)
                        }
                        boardSizefield =
                            textField("8") {
                                it.width(50f)
                                maxLength = 2
                                messageText = "Max 16"
                                alignment = Align.center

                                // Check that input is a number
                                textFieldFilter = TextField.TextFieldFilter { _, c -> c.isDigit() }

                                setTextFieldListener { field, _ ->
                                    val cursorPosition = field.cursorPosition
                                    field.text =
                                        field.text.filter { it.isDigit() }
                                    field.setCursorPosition(cursorPosition)
                                }
                            }
                    }
                add(boardField).pad(10f)
                row()

                textButton("Apply and return") {
                    it.padTop(100f)
                    onClick {
                        val enteredNumber = boardSizefield.text.toIntOrNull()

                        if (enteredNumber != null && enteredNumber in 8..16) {
                            onApply(fogOfWarCheckBox.isChecked, enteredNumber)
                        } else {
                            toastManager.showError("Board size must be between 8 and 16")
                        }
                    }
                }
            }

        stage.addActor(root)
        toastManager = ToastManager(stage)

        val fogSetting = gameSettings["fogOfWar"] as? Boolean ?: false
        fogOfWarCheckBox.isChecked = fogSetting

        val boardSize = gameSettings["boardSize"] as? Int ?: 8
        boardSizefield.text = boardSize.toString()

        Gdx.input.inputProcessor = stage
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
