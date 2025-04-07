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
    private lateinit var stage: Stage

    var onCreateLobbyButtonClicked: () -> Unit = {}
    var onJoinGameButtonClicked: () -> Unit = {}

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

                textButton("Create Lobby") {
                    it.padBottom(5f).width(200f)
                    onClick { onCreateLobbyButtonClicked() }
                }
                row()

                textButton("Join Game") {
                    it.width(200f)
                    onClick { onJoinGameButtonClicked() }
                }
                row()
                textButton("Exit") { onClick { Gdx.app.exit() } }
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
}
