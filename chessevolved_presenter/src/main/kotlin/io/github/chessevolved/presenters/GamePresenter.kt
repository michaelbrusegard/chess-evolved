package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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

    private val boardSize = 8 // TODO: Get from GameSettings?
    private val pixelSize = view.getBoardSize().toInt() / boardSize
    private var boardScreenPosX = 0
    private var boardScreenPosY = 0

    init {
        view.init()

        updateBoardPosition(Gdx.graphics.width, Gdx.graphics.height)

        engine.addSystem(
            BoardRenderingSystem(
                view.getBatch(),
                boardSize,
                pixelSize,
                boardScreenPosX,
                boardScreenPosY,
            ),
        )

        setupBoard()
    }

    private fun updateBoardPosition(
        width: Int,
        height: Int,
    ) {
        boardScreenPosX = (width - (boardSize * pixelSize)) / 2
        boardScreenPosY = (height - (boardSize * pixelSize)) / 2
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
        view.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        view.resize(width, height)
        updateBoardPosition(width, height)
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
