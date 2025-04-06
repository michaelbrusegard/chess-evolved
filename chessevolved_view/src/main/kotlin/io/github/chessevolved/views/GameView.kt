package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.scene2d.scene2d
import ktx.scene2d.table

class GameView : IView {
    private val spriteBatch = SpriteBatch()
    private val stage = Stage(FitViewport(800f, 800f), spriteBatch)

    override fun init() {
        val root =
            scene2d.table {
                setFillParent(true)
                defaults().expand()
            }
        stage.addActor(root)
    }

    fun beginBatch() {
        stage.viewport.apply()
        spriteBatch.begin()
    }

    fun endBatch() {
        spriteBatch.end()
        stage.draw()
    }

    override fun render() {
        stage.act(Gdx.graphics.deltaTime)
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        spriteBatch.dispose()
        stage.dispose()
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = stage
    }

    fun getBatch(): SpriteBatch = spriteBatch

    fun getBoardSize(): Float = minOf(stage.viewport.screenWidth, stage.viewport.screenHeight).toFloat()
}
