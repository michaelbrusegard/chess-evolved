package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.textField

class JoinGameView {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private lateinit var inputField: TextField
    private lateinit var toastContainer: Table

    var onJoinButtonClicked: (String) -> Unit = {}
    var onReturnButtonClicked: () -> Unit = {}

    init {
        setupUI()
        Gdx.input.inputProcessor = stage
    }

    fun setupUI() {
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
                        it.width(200f)
                    }
                row()

                textButton("Join Game") {
                    it.width(150f).padTop(20f)
                    onClick {
                        onJoinButtonClicked(inputField.text)
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

        toastContainer =
            scene2d.table {
                setFillParent(true)
                top()
                padTop(20f)
            }

        stage.addActor(root)
        stage.addActor(toastContainer)
    }

    fun showJoinSuccess() {
        showToast("Successfully joined the lobby!")
    }

    fun showJoinError(message: String) {
        showToast("Error: $message")
    }

    private fun showToast(message: String) {
        toastContainer.clear()

        val toast =
            scene2d.table {
                // Set table color directly
                setColor(0.2f, 0.2f, 0.2f, 0.8f)
                pad(10f)

                label(message) {
                    it.width(300f)
                    it.align(Align.center)
                }
            }

        toastContainer.add(toast).width(300f)

        // Fade in, stay visible for 3 seconds, then fade out
        toast.color.a = 0f
        toast.addAction(
            Actions.sequence(
                Actions.fadeIn(0.5f),
                Actions.delay(3f),
                Actions.fadeOut(0.5f),
                Actions.run { toastContainer.clear() },
            ),
        )
    }

    fun getLobbyId(): String = inputField.text

    fun render() {
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    fun resize(
        width: Int,
        height: Int,
    ) {
        stage.viewport.update(width, height, true)
    }

    fun dispose() {
        stage.dispose()
    }
}
