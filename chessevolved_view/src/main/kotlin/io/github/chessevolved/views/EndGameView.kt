package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

class EndGameView : IView {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))

    /**
     * Weather to show win or loss
     *
     * true = win
     *
     * false = loss
     */
    var endGameStatus: Boolean = false
        set(value) {
            field = value
        }

    var onReturnToMenu: () -> Unit = {}
    var onRematch: () -> Unit = {}

    override fun init() {
        val root =
            scene2d.table {
                // Set size of layout parent to screen.
                setFillParent(true)
                // Set padding to 10f
                defaults().pad(10f)

                // Perhaps replace label in the future with a logo. Or custom sprite text.
                if (endGameStatus) {
                    label("Congratulations!") { it.padBottom(20f).center() }
                    row()
                    label("You won!") { it.padBottom(20f).center() }
                } else {
                    label("Better luck next time!") { it.padBottom(20f).center() }
                    row()
                    label("You lost!") { it.padBottom(20f).center() }
                }
                row()
                textButton("Return to menu") {
                    it.padBottom(5f)
                    onClick { onReturnToMenu() }
                }
                row()
                textButton("Request rematch") {
                    onClick { onRematch() }
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
}
