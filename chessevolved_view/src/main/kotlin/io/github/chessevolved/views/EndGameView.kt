package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

class EndGameView : IView {
    private lateinit var stage: Stage
    private lateinit var statusLabel1: Label
    private lateinit var statusLabel2: Label
    private lateinit var rematchLabel: Label
    private lateinit var rematchButton: TextButton

    var endGameStatus: Boolean = false
        set(value) {
            field = value
            if (::stage.isInitialized) {
                updateStatusLabels()
            }
        }

    var onReturnToMenuClicked: () -> Unit = {}
    var onRematchClicked: () -> Unit = {}

    override fun init() {
        stage =
            Stage(
                FitViewport(
                    Gdx.graphics.width.toFloat(),
                    Gdx.graphics.height.toFloat(),
                ),
            )

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f).center()
                rematchLabel =
                    label("Waiting for rematch request...") { it.padBottom(10f) }

                row()
                statusLabel1 = label("") { it.padBottom(5f) }
                row()
                statusLabel2 = label("") { it.padBottom(20f) }
                row()

                textButton("Return to Menu") {
                    it.padBottom(5f)
                    onClick { onReturnToMenuClicked() }
                }
                row()
                rematchButton = textButton("Request Rematch") { onClick { onRematchClicked() } }
            }

        stage.addActor(root)
        updateStatusLabels()
    }

    private fun updateStatusLabels() {
        if (endGameStatus) {
            statusLabel1.setText("Congratulations!")
            statusLabel2.setText("You won!")
        } else {
            statusLabel1.setText("Better luck next time!")
            statusLabel2.setText("You lost!")
        }
    }

    fun disableRematchButton() {
        rematchButton.isDisabled = true
        rematchButton.clearListeners()
        rematchButton.setColor(0f, 0f, 0f, 0.3f)
    }

    fun updateRematchText(text: String) {
        rematchLabel.setText(text)
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
