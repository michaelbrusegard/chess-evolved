package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
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
    private lateinit var winInformationLabelTop: Label
    private lateinit var winInformationLabelBottom: Label
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

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport

    override fun init() {
        val screenRatio = Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        viewport = FitViewport(500f, 500f / screenRatio, camera)

        stage = Stage(viewport)

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f).center()
                rematchLabel =
                    label("Waiting for rematch request...") { it.padBottom(10f) }

                row()
                winInformationLabelTop = label("") { it.padBottom(5f) }
                row()
                winInformationLabelBottom = label("") { it.padBottom(20f) }
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
            winInformationLabelTop.setText("Congratulations!")
            winInformationLabelBottom.setText("You won!")
        } else {
            winInformationLabelTop.setText("Better luck next time!")
            winInformationLabelBottom.setText("You lost!")
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
