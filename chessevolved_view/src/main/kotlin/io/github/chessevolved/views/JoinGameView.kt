package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.*

class JoinGameView {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private lateinit var inputField: TextField

    var onJoinButtonClicked: (String) -> Unit = {}
    var onReturnButtonClicked: () -> Unit = {}

    init {
        setupUI()
        Gdx.input.inputProcessor = stage
    }

    fun setupUI() {
        val root = scene2d.table {
            setFillParent(true)
            defaults().pad(20f).space(10f)

            label("Join Game", "title") {
                it.padBottom(40f)
            }
            row()

            label("Enter Lobby Code:")
            row()

            inputField = textField("") {
                it.width(300f)
            }
            row()

            table {
                defaults().pad(10f).width(200f)

                textButton("Join Game") {
                    onClick {
                        onJoinButtonClicked(inputField.text)
                    }
                }

                textButton("Return to Menu") {
                    onClick {
                        onReturnButtonClicked()
                    }
                }
            }
        }

        stage.addActor(root)
    }

    fun showJoinSuccess() {
        scene2d.dialog("Success", "dialog") {
            text("Successfully joined the lobby!")
            button("OK") {
                onClick { hide() }
            }
        }.show(stage)
    }

    fun showJoinError(message: String) {
        scene2d.dialog("Error", "dialog") {
            text(message)
            button("OK") {
                onClick { hide() }
            }
        }.show(stage)
    }

    fun getLobbyId(): String = inputField.text

    fun render() {
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    fun dispose() {
        stage.dispose()
    }
}
