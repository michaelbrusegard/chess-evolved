package io.github.chessevolved.presenters

import com.badlogic.ashley.core.Entity
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
import io.github.chessevolved.components.GameState
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.SerializableBoardSquare
import io.github.chessevolved.components.SerializablePiece
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.entities.BoardSquareFactory
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.serialization.GameStateSerializer
import io.github.chessevolved.singletons.ComponentMappers
import io.github.chessevolved.singletons.ECSEngine
import io.github.chessevolved.singletons.EntityFamilies
import io.github.chessevolved.singletons.PlayerGameplayManager
import io.github.chessevolved.systems.AvailablePositionSystem
import io.github.chessevolved.systems.MovementSystem
import io.github.chessevolved.systems.RenderingSystem
import io.github.chessevolved.views.GameView

class GamePresenter(
    private val view: GameView,
    private val navigator: Navigator,
    private val assetManager: AssetManager,
) : IPresenter {
    private val engine = ECSEngine

    private val pieceFactory = PieceFactory(engine, assetManager)
    private val boardSquareFactory = BoardSquareFactory(engine, assetManager)

    private val playerGameplayManager: PlayerGameplayManager

    private val gameCamera = OrthographicCamera()
    private val boardWorldSize = 10
    private val gameViewport: Viewport =
        FitViewport(boardWorldSize.toFloat(), boardWorldSize.toFloat(), gameCamera)
    private val gameBatch: SpriteBatch
    private val gameStage: Stage

    private val gameState: GameState

    private val movementSystem: MovementSystem
    private val availablePositionSystem: AvailablePositionSystem
    private val renderingSystem: RenderingSystem

    // Flags
    private var pieceIsSelected: Boolean = false
    private var pieceSelectedPos: Position = Position(0, 0)
    private var selectedPiece: Entity? = null
    private var selectedPieceAvailablePositions: MutableList<Position> = ArrayList()

    init {
        view.init()
        gameBatch = view.getGameBatch()
        gameStage = view.getStage()

        playerGameplayManager = PlayerGameplayManager

        view.setOnPieceClickedListener { x, y ->
            handlePieceClick(Position(x, y))
        }

        view.setOnBoardClickedListener { x, y ->
            handleBoardClick((Position(x, y)))
        }

        gameCamera.position.set(boardWorldSize / 2f, boardWorldSize / 2f, 0f)
        gameCamera.update()

        renderingSystem = RenderingSystem(gameBatch)
        engine.addSystem(renderingSystem)

        gameState = GameState(ArrayList(), ArrayList())

        loadRequiredAssets()
        assetManager.finishLoading()

        setupBoard()

        availablePositionSystem = AvailablePositionSystem()
        movementSystem = MovementSystem()

        // If we do not call this the board will not be displayed
        resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    private fun loadRequiredAssets() {
        assetManager.load("board/black-tile.png", Texture::class.java)
        assetManager.load("board/white-tile.png", Texture::class.java)

        assetManager.load("pieces/rook-black.png", Texture::class.java)
        assetManager.load("pieces/rook-white.png", Texture::class.java)
        assetManager.load("pieces/pawn-white.png", Texture::class.java)
        assetManager.load("pieces/pawn-black.png", Texture::class.java)
        assetManager.load("pieces/bishop-white.png", Texture::class.java)
        assetManager.load("pieces/bishop-black.png", Texture::class.java)
        assetManager.load("pieces/knight-white.png", Texture::class.java)
        assetManager.load("pieces/knight-black.png", Texture::class.java)
        // PlayerColor.entries.forEach { color ->
        //     PieceType.entries.forEach { type ->
        //             val colorStr = color.name.lowercase()
        //             val typeStr = type.name.lowercase()
        //             val filename = "pieces/$colorStr-$typeStr.png"
        //             assetManager.load(filename, Texture::class.java)
        //     }
        // }
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
                ) {clickedPosition -> handleBoardClick(clickedPosition)}
            }
        }

        val startX: Int = (boardWorldSize / 2) - 4
        for (i in startX until startX+8) {
            val pieceP1 = pieceFactory.createPawn(
                true,
                Position(i, 1),
                PlayerColor.WHITE,
                gameStage
            ) {clickedPosition -> handlePieceClick(clickedPosition)}
            playerGameplayManager.player1AddPiece(pieceP1)

            val pieceP2 = pieceFactory.createPawn(
                false,
                Position(i, boardWorldSize-2),
                PlayerColor.BLACK,
                gameStage
            ) {clickedPosition -> handlePieceClick(clickedPosition)}
            playerGameplayManager.player2AddPiece(pieceP2)
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

        // In case we want part of the UI to be scene2d, we render the view on top
        gameStage.viewport = gameViewport
        gameStage.act(Gdx.graphics.deltaTime)
        gameStage.draw()

        // view.render()
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
        view.setInputProcessor()
    }

    private fun handlePieceClick(pos: Position) {
        // println("PiecePos, x: " + pos.x + ", y: " + pos.y)
        if (pieceIsSelected && pieceSelectedPos == pos) {
            renderingSystem.defaultBoardSquaresState()
            pieceIsSelected = false
        } else {
            renderingSystem.defaultBoardSquaresState()
            pieceIsSelected = true
            pieceSelectedPos = pos

            selectedPiece = EntityFamilies.getPieceEntities().find { entity ->
                val entityPos = ComponentMappers.posMap.get(entity).position
                entityPos == pos
            }

            val piecePositionComponent = ComponentMappers.posMap.get(selectedPiece)
            println(piecePositionComponent.position)


            val pieceCol = ComponentMappers.colorMap.get(selectedPiece).color
            val pieceMoves = ComponentMappers.movementMap.get(selectedPiece)

            selectedPieceAvailablePositions = availablePositionSystem.checkAvailablePositions(pieceCol, pos, pieceMoves, boardWorldSize)

            renderingSystem.changeBoardsForPositions(selectedPieceAvailablePositions)
        }
    }

    private fun handleBoardClick(pos: Position) {
        // println("BoardPos, x: " + pos.x + ", y: " + pos.y)

        if (pieceIsSelected) {
            if (selectedPieceAvailablePositions.isNotEmpty()) {
                movementSystem.movePieceToPos(selectedPiece, pos, selectedPieceAvailablePositions)
                pieceIsSelected = false
                renderingSystem.defaultBoardSquaresState()
            }
        }
    }
}
