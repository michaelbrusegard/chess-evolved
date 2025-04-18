package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class GameUIView(
    /**
     * This variable can be used to make sure the ui doesn't overlap the game-board.
     */
    private val gameViewport: Viewport,
    private val gameCamera: OrthographicCamera,
) : IView {
    private lateinit var stage: Stage
    private lateinit var viewport: Viewport

    override fun init() {
        viewport =
            FitViewport(
                Gdx.graphics.width.toFloat(),
                Gdx.graphics.height.toFloat(),
            )

        stage =
            Stage()

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f)
                label("Settings 1") {
                    it.colspan(2).padBottom(50f).center()
                    setColor(1f, 0f, 0f, 0.5f)
                }
                row()
                label("Settings 2") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings 3") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings 4") {
                    it.colspan(2).padBottom(50f).center()
                }
                row()
                label("Settings 5") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings 6") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings") { it.colspan(2).padBottom(50f).center() }
                row()
                label("Settings") { it.colspan(2).padBottom(50f).center() }
                row()

                label("Fog of War:") { it.right() }
                row()

                label("Board Size (8-16):") { it.right() }
                row()
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
        throw IllegalAccessError("setInputProcessor is not allowed for GameUIView. Call GameView's setInputProcessor instead.")
        // Gdx.input.inputProcessor = stage
    }

    fun getStage() = stage
}
