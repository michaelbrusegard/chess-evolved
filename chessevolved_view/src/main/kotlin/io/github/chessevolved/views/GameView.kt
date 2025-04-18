package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport

class GameView(
    private val uiStage: Stage,
    private val gameViewport: Viewport,
) : IView {
    private lateinit var gameStage: Stage

    private val inputMultiplexer = InputMultiplexer()

    override fun init() {
        gameStage = Stage(gameViewport)
        inputMultiplexer.addProcessor(uiStage)
        inputMultiplexer.addProcessor(gameStage)
    }

    fun getStage(): Stage = gameStage

    override fun render() {
        gameStage.act(Gdx.graphics.deltaTime)
        gameStage.draw()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        gameStage.viewport.update(width, height, true)
    }

    override fun dispose() {
        gameStage.dispose()
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = inputMultiplexer
    }
}
