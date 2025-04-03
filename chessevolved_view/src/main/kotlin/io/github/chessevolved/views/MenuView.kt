package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

class MenuView : IView {
    private lateinit var stage : Stage

    var onCreateLobbyViewButtonClicked: () -> Unit = {}
    var onJoinGameViewButtonClicked: () -> Unit = {}

    override fun init() {
        stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))

        val root =
            scene2d.table {
                // Set size of layout parent to screen.
                setFillParent(true)
                // Set padding to 10f
                defaults().pad(10f)

                // Perhaps replace label in the future with a logo. Or custom sprite text.
                label("Chess Evolved!") { it.padBottom(20f) }
                row()
                textButton("Create a Lobby") {
                    it.padBottom(5f)
                    onClick { onCreateLobbyViewButtonClicked() }
                }
                row()
                textButton("Join Game") {
                    onClick { onJoinGameViewButtonClicked() }
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
        // The Presenter should activate the input processor for this stage.
        Gdx.input.inputProcessor = stage
    }
}
