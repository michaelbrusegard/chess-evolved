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

class LobbyView(
    val lobbyCode: String,
) : IView {
    private lateinit var stage: Stage
    private lateinit var secondPlayerStatusLabel: Label
    private lateinit var startGameButton: TextButton

    var onLeaveButtonClicked: () -> Unit = {}
    var onOpenSettingsButtonClicked: () -> Unit = {}
    var onStartGameButtonClicked: () -> Unit = {}

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

                label("Chess Evolved!") { it.padBottom(20f) }
                row()

                label("Lobby Code: $lobbyCode") { it.padBottom(10f) }
                row()

                secondPlayerStatusLabel =
                    label("Waiting for second player...") { it.padBottom(10f) }
                row()

                startGameButton =
                    textButton("Start Game") {
                        it.padBottom(20f)
                    }
                row()

                textButton("Lobby Settings") {
                    onClick { onOpenSettingsButtonClicked() }
                }
                row()

                textButton("Leave Lobby") { onClick { onLeaveButtonClicked() } }
            }

        stage.addActor(root)
        setSecondPlayerConnected(true)
    }

    fun setSecondPlayerConnected(isConnected: Boolean) {
        if (isConnected) {
            secondPlayerStatusLabel.setText("Second player connected!")
            startGameButton.isDisabled = false
            startGameButton.onClick { onStartGameButtonClicked() }
        } else {
            secondPlayerStatusLabel.setText("Waiting for second player...")
            startGameButton.isDisabled = true
            startGameButton.clearListeners()
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
