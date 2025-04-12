package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport

fun interface OnPieceClickedListener {
    fun onClick(
        x: Int,
        y: Int,
    )
}

class GameView : IView {
    private lateinit var gameStage: Stage
    private lateinit var uiStage: Stage

    private val gameBatch = SpriteBatch()

    private var clickListener: OnPieceClickedListener? = null

    private val gameViewport = ScreenViewport()
    private val uiViewport = ScreenViewport()

    private val inputMultiplexer = InputMultiplexer()

    override fun init() {
        gameStage = Stage(gameViewport, gameBatch)
        uiStage = Stage(uiViewport, gameBatch)
    }

    fun getGameBatch(): SpriteBatch = gameBatch

    fun getStage(): Stage = gameStage

    fun setOnPieceClickedListener(listener: OnPieceClickedListener) {
        this.clickListener = listener
    }

    override fun render() {
        gameStage.viewport = gameViewport
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
        gameBatch.dispose()
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = gameStage
    }

    fun getClickListener(): OnPieceClickedListener? = clickListener
}
