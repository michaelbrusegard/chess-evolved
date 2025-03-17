package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.textField

class JoinGameView : IView {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private lateinit var inputField: TextField
    private lateinit var toastManager: ToastManager

    var onJoinButtonClicked: (String) -> Unit = {}
    var onReturnButtonClicked: () -> Unit = {}

    override fun init() {
        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f)

                label("Join Game", "title") {
                    it.padBottom(20f)
                }
                row()

                label("Enter Lobby Code:")
                row()

                inputField =
                    textField("") {
                        it.width(125f)
                        maxLength = 6
                        messageText = "XXXXXX"

                        textFieldFilter =
                            TextField.TextFieldFilter { _, c ->
                                c.isLetterOrDigit()
                            }

                        setTextFieldListener { field, _ ->
                            val cursorPosition = field.cursorPosition
                            field.text = field.text.uppercase()
                            field.setCursorPosition(cursorPosition)
                        }
                    }
                row()

                textButton("Join Game") {
                    it.width(150f).padTop(20f)
                    onClick {
                        val code = inputField.text
                        if (code.length == 6) {
                            onJoinButtonClicked(code)
                        } else {
                            toastManager.showError("Lobby code must be 6 characters")
                        }
                    }
                }
                row()

                textButton("Return to Menu") {
                    it.width(200f).padTop(10f)
                    onClick {
                        onReturnButtonClicked()
                    }
                }
            }

        stage.addActor(root)
        toastManager = ToastManager(stage)

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

    fun showJoinSuccess() {
        toastManager.showSuccess("Successfully joined the lobby!")
    }

    fun showJoinError(message: String) {
        toastManager.showError("Error: $message")
    }
}
