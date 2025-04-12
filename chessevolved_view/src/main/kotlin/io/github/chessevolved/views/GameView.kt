package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport

fun interface OnPieceClickedListener {
    fun onClick(
        x: Int,
        y: Int,
    )
}

class GameView(
    private val uiStage: Stage,
    private val gameViewport: Viewport,
) : IView {
    private lateinit var gameStage: Stage

    /**
     * Click listener for when a chess-piece has been clicked on.
     */
    private var clickListener: OnPieceClickedListener? = null

    /**
     * Multiplexer to handle input from both gameViewport and uiViewport at the same time.
     */
    private val inputMultiplexer = InputMultiplexer()

    override fun init() {
        gameStage = Stage(gameViewport)
        inputMultiplexer.addProcessor(gameStage)
        inputMultiplexer.addProcessor(uiStage)
    }

    fun getStage(): Stage = gameStage

    fun setOnPieceClickedListener(listener: OnPieceClickedListener) {
        this.clickListener = listener
    }

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

    fun getClickListener(): OnPieceClickedListener? = clickListener
}
