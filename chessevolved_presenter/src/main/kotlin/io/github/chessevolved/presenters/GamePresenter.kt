package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.chessevolved.Navigator
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.entities.BoardSquareFactory
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.singletons.ECSEngine
import io.github.chessevolved.systems.BoardRenderingSystem
import io.github.chessevolved.views.GameView

class GamePresenter(
    private val view: GameView,
    private val navigator: Navigator,
) : IPresenter {
    private val engine = ECSEngine
    private val pieceFactory = PieceFactory(engine)
    private val boardSquareFactory = BoardSquareFactory(engine)

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(8f, 8f, gameCamera)
    private val gameBatch: SpriteBatch

    private var boardRenderingSystem: BoardRenderingSystem

    private val boardWorldSize = 8f

    init {
        view.init()
        gameBatch = view.getGameBatch()

        gameCamera.position.set(boardWorldSize / 2f, boardWorldSize / 2f, 0f)
        gameCamera.update()

        boardRenderingSystem =
            BoardRenderingSystem(
                gameBatch,
                boardWorldSize.toInt(),
            )
        engine.addSystem(boardRenderingSystem)
        // Add other game logic systems here

        setupBoard()
    }

    private fun setupBoard() {
        pieceFactory.createRook(
            Position(4, 4),
            PlayerColor.BLACK,
        )
    }

    override fun update(dt: Float) {
        engine.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        gameViewport.apply()
        gameBatch.projectionMatrix = gameCamera.combined
        gameBatch.begin()
        boardRenderingSystem.update(0f)
        gameBatch.end()
        view.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        gameViewport.update(width, height, false)
        view.resize(width, height)
    }

    override fun dispose() {
        view.dispose()
        engine.removeAllEntities()
        engine.systems.forEach { engine.removeSystem(it) }
    }

    override fun setInputProcessor() {
        view.setInputProcessor()
    }
}
