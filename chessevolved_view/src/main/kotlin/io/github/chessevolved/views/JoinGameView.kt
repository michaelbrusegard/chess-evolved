package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
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
    private lateinit var stage: Stage
    private lateinit var inputField: TextField
    private lateinit var toastManager: ToastManager

    var onJoinButtonClicked: (String) -> Unit = {}
    var onReturnButtonClicked: () -> Unit = {}

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport

    override fun init() {
        val screenRatio = Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        viewport = FitViewport(500f, 500f / screenRatio, camera)

        stage = Stage(viewport)

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
                defaults().pad(10f).center()

                label("Join Game") { it.padBottom(20f) }
                row()

                label("Enter Lobby Code:")
                row()

                inputField =
                    textField {
                        it.width(125f)
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

                textButton("Join Game") {
                    it.width(150f).padTop(20f)
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

                textButton("Return to Menu") {
                    it.width(200f)
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
