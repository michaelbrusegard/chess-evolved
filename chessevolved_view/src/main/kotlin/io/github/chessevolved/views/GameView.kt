package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage

fun interface OnPieceClickedListener {
    fun onClick(
        x: Int,
        y: Int,
    )
}

fun interface OnBoardClickedListener {
    fun onClick(
        x: Int,
        y: Int,
    )
}

class GameView : IView {
    private lateinit var stage: Stage

    private val gameBatch = SpriteBatch()

    /**
     * Click listener for when a chess-piece has been clicked on.
     */
    private var clickListener: OnPieceClickedListener? = null

    private var boardClickListener: OnBoardClickedListener? = null

    private val screenViewport = ScreenViewport()

    override fun init() {
        gameStage = Stage(gameViewport)
        inputMultiplexer.addProcessor(gameStage)
        inputMultiplexer.addProcessor(uiStage)
    }

    fun getStage(): Stage = gameStage

    fun setOnPieceClickedListener(listener: OnPieceClickedListener) {
        this.clickListener = listener
    }

    fun setOnBoardClickedListener(listener: OnBoardClickedListener) {
        this.boardClickListener = listener
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
}
