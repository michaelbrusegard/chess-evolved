package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport

fun interface OnPieceClickedListener {
    fun onClick(x: Int, y: Int)
}

class GameView : IView {
    private lateinit var stage: Stage

    private val gameBatch = SpriteBatch()

    private var clickListener: OnPieceClickedListener? = null

    private val screenViewport = ScreenViewport()

    override fun init() {
        stage = Stage(screenViewport)
    }

    fun getGameBatch(): SpriteBatch = gameBatch

    fun getStage(): Stage = stage

    fun setOnPieceClickedListener(listener: OnPieceClickedListener) {
        this.clickListener = listener
    }

    override fun render() {
        stage.viewport = screenViewport
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
        gameBatch.dispose()
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = stage
    }

    fun getClickListener(): OnPieceClickedListener? = clickListener
}
