package io.github.chessevolved.presenters

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.chessevolved.Navigator
import io.github.chessevolved.components.CaneBeCapturedComponent
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.entities.BoardSquareFactory
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.serialization.GameStateSerializer
import io.github.chessevolved.singletons.ECSEngine
import io.github.chessevolved.systems.CaptureSystem
import io.github.chessevolved.systems.MovementSystem
import io.github.chessevolved.systems.RenderingSystem
import io.github.chessevolved.systems.SelectionEntityListener
import io.github.chessevolved.views.GameUIView
import io.github.chessevolved.views.GameView

class GamePresenter(
    private val navigator: Navigator,
    private val assetManager: AssetManager,
) : IPresenter {
    private val engine = ECSEngine

    private val pieceFactory = PieceFactory(engine, assetManager)
    private val boardSquareFactory = BoardSquareFactory(engine, assetManager)

    private val gameCamera = OrthographicCamera()
    private val boardWorldSize = 8

    private val gameViewport: Viewport =
        FitViewport(boardWorldSize.toFloat(), boardWorldSize.toFloat(), gameCamera)
    private lateinit var gameUIView: GameUIView
    private lateinit var gameBoardView: GameView
    private val gameBatch: SpriteBatch = SpriteBatch()
    private lateinit var gameStage: Stage

    private val movementSystem: MovementSystem
    private val renderingSystem: RenderingSystem
    private val selectionListener: SelectionEntityListener
    private val captureSystem: CaptureSystem

    init {
        setupGameView()

        renderingSystem = RenderingSystem(gameBatch)
        engine.addSystem(renderingSystem)

        movementSystem = MovementSystem()
        engine.addSystem(movementSystem)

        selectionListener = SelectionEntityListener(boardWorldSize)
        engine.addEntityListener(Family.all(SelectionComponent::class.java).get(), selectionListener)

        captureSystem = CaptureSystem()
        engine.addSystem(captureSystem)

        loadRequiredAssets()
        assetManager.finishLoading()

        setupBoard()

        // If we do not call this the board will not be displayed
        resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    private fun loadRequiredAssets() {
        assetManager.load("board/black-tile.png", Texture::class.java)
        assetManager.load("board/white-tile.png", Texture::class.java)

        PlayerColor.entries.forEach { color ->
            PieceType.entries.forEach { type ->
                val colorStr = color.name.lowercase()
                val typeStr = type.name.lowercase()
                val filename = "pieces/$typeStr-$colorStr.png"
                assetManager.load(filename, Texture::class.java)
            }
        }
    }

    private fun setupGameView() {
        gameUIView = GameUIView(gameViewport, gameCamera)
        gameUIView.init()

        gameBoardView = GameView(gameUIView.getStage(), gameViewport)
        gameBoardView.init()

        gameStage = gameBoardView.getStage()

        gameCamera.position.set(boardWorldSize / 2f, boardWorldSize / 2f, 0f)
        gameCamera.update()
    }

    private fun setupBoard() {
        for (y in 0 until boardWorldSize) {
            for (x in 0 until boardWorldSize) {
                val tileColor =
                    if ((x + y) % 2 == 0) PlayerColor.BLACK else PlayerColor.WHITE
                boardSquareFactory.createBoardSquare(
                    Position(x, y),
                    WeatherEvent.NONE,
                    tileColor,
                    gameStage,
                ) { clickedPosition -> handleBoardClick(clickedPosition) }
            }
        }

        val startX: Int = (boardWorldSize / 2) - 4
        for (startPos in startX until startX + 8) {
            pieceFactory.createPawn(
                true,
                Position(startPos, 1),
                PlayerColor.WHITE,
                gameStage,
            ) { clickedPosition -> handlePieceClick(clickedPosition) }

            pieceFactory.createPawn(
                false,
                Position(startPos, boardWorldSize - 2),
                PlayerColor.BLACK,
                gameStage,
            ) { clickedPosition -> handlePieceClick(clickedPosition) }

            when (startPos) {
                startX -> {
                    for (j in listOf(0, 7)) {
                        pieceFactory.createRook(
                            Position(startX + j, 0),
                            PlayerColor.WHITE,
                            gameStage,
                        ) { clickedPosition -> handlePieceClick(clickedPosition) }

                        pieceFactory.createRook(
                            Position(startX + j, boardWorldSize - 1),
                            PlayerColor.BLACK,
                            gameStage,
                        ) { clickedPosition -> handlePieceClick(clickedPosition) }
                    }
                }
                startX + 1 -> {
                    for (j in listOf(1, 6)) {
                        pieceFactory.createKnight(
                            Position(startX + j, 0),
                            PlayerColor.WHITE,
                            gameStage,
                        ) { clickedPosition -> handlePieceClick(clickedPosition) }

                        pieceFactory.createKnight(
                            Position(startX + j, boardWorldSize - 1),
                            PlayerColor.BLACK,
                            gameStage,
                        ) { clickedPosition -> handlePieceClick(clickedPosition) }
                    }
                }
                startX + 2 -> {
                    for (j in listOf(2, 5)) {
                        pieceFactory.createBishop(
                            Position(startX + j, 0),
                            PlayerColor.WHITE,
                            gameStage,
                        ) { clickedPosition -> handlePieceClick(clickedPosition) }

                        pieceFactory.createBishop(
                            Position(startX + j, boardWorldSize - 1),
                            PlayerColor.BLACK,
                            gameStage,
                        ) { clickedPosition -> handlePieceClick(clickedPosition) }
                    }
                }
                startX + 3 -> {
                    pieceFactory.createQueen(
                        Position(startPos, 0),
                        PlayerColor.WHITE,
                        gameStage,
                    ) { clickedPosition -> handlePieceClick(clickedPosition) }

                    pieceFactory.createQueen(
                        Position(startPos, boardWorldSize - 1),
                        PlayerColor.BLACK,
                        gameStage,
                    ) { clickedPosition -> handlePieceClick(clickedPosition) }
                }
                startX + 4 -> {
                    pieceFactory.createKing(
                        Position(startPos, 0),
                        PlayerColor.WHITE,
                        gameStage,
                    ) { clickedPosition -> handlePieceClick(clickedPosition) }

                    pieceFactory.createKing(
                        Position(startPos, boardWorldSize - 1),
                        PlayerColor.BLACK,
                        gameStage,
                    ) { clickedPosition -> handlePieceClick(clickedPosition) }
                }
                else -> {}
            }
        }
    }

    override fun render(sb: SpriteBatch) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        gameViewport.apply()
        gameBatch.projectionMatrix = gameCamera.combined

        gameBatch.begin()
        engine.update(Gdx.graphics.deltaTime)
        gameBatch.end()

        gameBoardView.render()
        gameUIView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        gameViewport.update(width, height, false)
        gameBoardView.resize(width, height)
        // TODO: Do some resize logic for GameUIView as well.
    }

    override fun dispose() {
        gameBoardView.dispose()
        gameUIView.dispose()
        gameBatch.dispose()
        engine.removeAllEntities()
        unloadAssets()
    }

    private fun unloadAssets() {
        if (assetManager.isLoaded("board/black-tile.png")) {
            assetManager.unload("board/black-tile.png")
        }
        if (assetManager.isLoaded("board/white-tile.png")) {
            assetManager.unload("board/white-tile.png")
        }

        PlayerColor.entries.forEach { color ->
            PieceType.entries.forEach { type ->
                val colorStr = color.name.lowercase()
                val typeStr = type.name.lowercase()
                val filename = "pieces/$typeStr-$colorStr.png"
                if (assetManager.isLoaded(filename)) {
                    assetManager.unload(filename)
                }
            }
        }
    }

    override fun setInputProcessor() {
        gameBoardView.setInputProcessor()
    }

    private fun handlePieceClick(pos: Position) {
        println("PiecePos, x: " + pos.x + ", y: " + pos.y)

        val piece =
            engine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get()).find { entity ->
                val position = PositionComponent.mapper.get(entity).position
                position == pos
            }

        if (piece != null) {
            val selectionComponent = piece.getComponent(SelectionComponent::class.java)
            val canBeCapturedComponent = piece.getComponent(CaneBeCapturedComponent::class.java)

            if (selectionComponent != null) {
                piece.remove(SelectionComponent::class.java)
            } else if (canBeCapturedComponent != null) {
                piece.add(CapturedComponent())
            } else {
                engine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get()).find { entity ->
                    entity.getComponent(SelectionComponent::class.java) != null
                }?.remove(SelectionComponent::class.java)
                piece.add(SelectionComponent())
            }
        }
    }

    private fun handleBoardClick(pos: Position) {
        println("BoardPos, x: " + pos.x + ", y: " + pos.y)
        engine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get()).find { entity ->
            entity.getComponent(SelectionComponent::class.java) != null
        }?.add(MovementIntentComponent(pos))
    }
}
