package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

class LobbyView(
    val lobbyCode: String,
) : IView {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))

    var onLeaveButtonClicked: () -> Unit = {}
    var onOpenSettingsButtonClicked: () -> Unit = {}
    var onStartGameButtonClicked: () -> Unit = {}

    private var secondPlayerStatusText = scene2d.label("Waiting for second player...")
    private var startGameButton =
        scene2d.textButton("Start Game!") {
            onClick { }
            setColor(0f, 0f, 0f, 0.3f)
        }

    override fun init() {
        // TODO: Check if second player is already in lobby, and if so, update secondPlayerStatusText.
        val root =
            scene2d.table {
                // Set size of layout parent to screen.
                setFillParent(true)
                // Set padding to 10f
                defaults().pad(10f)

                // Perhaps replace label in the future with a logo. Or custom sprite text.
                label("Chess Evolved!") { it.padBottom(20f) }
                row()

                label("Lobby Code: $lobbyCode")
                row()

                add(secondPlayerStatusText)
                row()

                add(startGameButton)
                row()

                textButton("Open Lobby Settings") {
                    onClick { onOpenSettingsButtonClicked() }
                }
                row()

                textButton("Leave Lobby") {
                    it.padBottom(5f)
                    onClick { onLeaveButtonClicked() }
                }
                row()
            }

        stage.addActor(root)
    }

    fun setSecondPlayerConnected(isConnected: Boolean) {
        if (isConnected) {
            secondPlayerStatusText.setText("Second player connected!")
            startGameButton.onClick { onStartGameButtonClicked() }
            startGameButton.color = scene2d.label("tex").color
        } else {
            secondPlayerStatusText.setText("Waiting for second player...")
            startGameButton.onClick { }
            startGameButton.setColor(0f, 0f, 0f, 0.3f)
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
