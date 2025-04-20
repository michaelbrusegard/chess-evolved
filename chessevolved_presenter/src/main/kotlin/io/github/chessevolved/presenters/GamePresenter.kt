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
import io.github.chessevolved.components.AbilityCardComponent
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.entities.AbilityItemFactory
import io.github.chessevolved.entities.BoardSquareFactory
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.enums.AbilityType
import io.github.chessevolved.enums.PieceType
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.enums.WeatherEvent
import io.github.chessevolved.singletons.EcsEngine
import io.github.chessevolved.singletons.EcsEntityMapper
import io.github.chessevolved.singletons.Game
import io.github.chessevolved.singletons.Game.subscribeToGameUpdates
import io.github.chessevolved.singletons.Game.unsubscribeFromGameUpdates
import io.github.chessevolved.singletons.GameSettings
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler
import io.github.chessevolved.systems.AbilitySystem
import io.github.chessevolved.systems.CaptureSystem
import io.github.chessevolved.systems.FowRenderingSystem
import io.github.chessevolved.systems.InputService
import io.github.chessevolved.systems.InputSystem
import io.github.chessevolved.systems.MovementSystem
import io.github.chessevolved.systems.RenderingSystem
import io.github.chessevolved.systems.SelectionEntityListener
import io.github.chessevolved.systems.VisualEffectSystem
import io.github.chessevolved.views.GameUIView
import io.github.chessevolved.views.GameView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GamePresenter(
    private val navigator: Navigator,
    private val assetManager: AssetManager,
) : IPresenter {
    private val engine = EcsEngine

    private val pieceFactory = PieceFactory(engine, assetManager)
    private val boardSquareFactory = BoardSquareFactory(engine, assetManager)
    private val abilityItemFactory = AbilityItemFactory(engine, assetManager)

    private val gameCamera = OrthographicCamera()
    private val gameUICamera = OrthographicCamera()
    private val boardWorldSize = 8

    private val gameViewport: Viewport =
        FitViewport(boardWorldSize.toFloat(), boardWorldSize.toFloat(), gameCamera)
    private val gameUIViewport: Viewport =
        FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), gameUICamera)
    private lateinit var gameUIView: GameUIView
    private lateinit var gameBoardView: GameView
    private val gameBatch: SpriteBatch = SpriteBatch()
    private lateinit var gameStage: Stage

    private val movementSystem: MovementSystem
    private val renderingSystem: RenderingSystem
    private val fowRenderingSystem: FowRenderingSystem
    private val selectionListener: SelectionEntityListener
    private val captureSystem: CaptureSystem
    private val inputSystem: InputSystem
    private val inputService = InputService()
    private val abilitySystem: AbilitySystem
    private val visualEffectSystem: VisualEffectSystem

    private var navigatingToEndGame = false

    init {
        setupGameView()

        renderingSystem = RenderingSystem(gameBatch)
        engine.addSystem(renderingSystem)

        movementSystem = MovementSystem(this::onTurnComplete)
        engine.addSystem(movementSystem)

        selectionListener = SelectionEntityListener(boardWorldSize)
        engine.addEntityListener(Family.all(SelectionComponent::class.java).get(), selectionListener)

        captureSystem = CaptureSystem()
        engine.addSystem(captureSystem)

        inputSystem = InputSystem()
        engine.addSystem(inputSystem)

        abilitySystem = AbilitySystem()
        engine.addSystem(abilitySystem)

        visualEffectSystem = VisualEffectSystem(gameBatch, assetManager)
        engine.addSystem(visualEffectSystem)

        loadRequiredAssets()
        assetManager.finishLoading()

        fowRenderingSystem = FowRenderingSystem(gameBatch, assetManager)
        engine.addSystem(fowRenderingSystem)

        visualEffectSystem.initializeAnimations()

        setupBoard()

        resize(Gdx.graphics.width, Gdx.graphics.height)

        val testAbilityCard = abilityItemFactory.createAbilityItem(AbilityType.EXPLOSION)
        AbilityCardComponent.mapper.get(testAbilityCard).isInInventory = true

        subscribeToGameUpdates(this.toString(), this::onGameStateUpdate)
    }

    private fun loadRequiredAssets() {
        assetManager.load("board/black-tile.png", Texture::class.java)
        assetManager.load("board/white-tile.png", Texture::class.java)
        assetManager.load("board/fow.png", Texture::class.java)

        PlayerColor.entries.forEach { color ->
            PieceType.entries.forEach { type ->
                val colorStr = color.name.lowercase()
                val typeStr = type.name.lowercase()
                val filename = "pieces/$typeStr-$colorStr.png"
                assetManager.load(filename, Texture::class.java)
            }
        }

        AbilityType.entries.forEach { ability ->
            val abilityName = ability.name.lowercase()
            println("abilities/cards/${abilityName}Card.png")
            assetManager.load("abilities/cards/${abilityName}Card.png", Texture::class.java)
        }
    }

    private fun setupGameView() {
        runBlocking {
            launch {
                Game.joinGame(Lobby.getLobbyId() ?: throw IllegalStateException("Can't join a game if not in a lobby first!"))
            }
        }

        gameUIView = GameUIView(gameUIViewport, GameSettings.clientPlayerColor == PlayerColor.WHITE, ::onSelectAbilityCardButtonClicked)
        gameUIView.init()

        gameBoardView = GameView(gameUIView.getStage(), gameViewport)
        gameBoardView.init()

        gameStage = gameBoardView.getStage()

        gameCamera.position.set(boardWorldSize / 2f, boardWorldSize / 2f, 1f)
        gameUICamera.position.set(boardWorldSize / 2f, boardWorldSize / 2f, 0f)
        gameCamera.update()
        gameUICamera.update()
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
                )
            }
        }

        val startX: Int = (boardWorldSize / 2) - 4
        for (startPos in startX until startX + 8) {
            pieceFactory
                .createPawn(
                    Position(startPos, 1),
                    GameSettings.clientPlayerColor,
                    gameStage,
                )

            pieceFactory
                .createPawn(
                    Position(startPos, boardWorldSize - 2),
                    GameSettings.opponentPlayerColor,
                    gameStage,
                )
        }

        val startXLocal: Int = (boardWorldSize / 2) - 4
        for (startPos in startXLocal until startXLocal + 8) {
            when (startPos) {
                startXLocal -> {
                    for (j in listOf(0, 7)) {
                        pieceFactory.createRook(
                            Position(startXLocal + j, 0),
                            GameSettings.clientPlayerColor,
                            gameStage,
                        )

                        pieceFactory.createRook(
                            Position(startXLocal + j, boardWorldSize - 1),
                            GameSettings.opponentPlayerColor,
                            gameStage,
                        )
                    }
                }
                startXLocal + 1 -> {
                    for (j in listOf(1, 6)) {
                        pieceFactory.createKnight(
                            Position(startXLocal + j, 0),
                            GameSettings.clientPlayerColor,
                            gameStage,
                        )

                        pieceFactory.createKnight(
                            Position(startXLocal + j, boardWorldSize - 1),
                            GameSettings.opponentPlayerColor,
                            gameStage,
                        )
                    }
                }
                startXLocal + 2 -> {
                    for (j in listOf(2, 5)) {
                        pieceFactory.createBishop(
                            Position(startXLocal + j, 0),
                            GameSettings.clientPlayerColor,
                            gameStage,
                        )

                        pieceFactory.createBishop(
                            Position(startXLocal + j, boardWorldSize - 1),
                            GameSettings.opponentPlayerColor,
                            gameStage,
                        )
                    }
                }
                startXLocal + 3 -> {
                    pieceFactory.createQueen(
                        Position(startPos, 0),
                        GameSettings.clientPlayerColor,
                        gameStage,
                    )

                    pieceFactory.createQueen(
                        Position(startPos, boardWorldSize - 1),
                        GameSettings.opponentPlayerColor,
                        gameStage,
                    )
                }
                startXLocal + 4 -> {
                    pieceFactory.createKing(
                        Position(startPos, 0),
                        GameSettings.clientPlayerColor,
                        gameStage,
                    )

                    pieceFactory.createKing(
                        Position(startPos, boardWorldSize - 1),
                        GameSettings.opponentPlayerColor,
                        gameStage,
                    )
                }
                else -> {}
            }
        }
    }

    private fun goToGameOverScreen(didWin: Boolean) {
        navigatingToEndGame = true
        navigator.navigateToEndGame(didWin)
    }

    override fun render(sb: SpriteBatch) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        gameViewport.apply()
        gameCamera.update()
        gameBatch.projectionMatrix = gameCamera.combined

        gameBatch.begin()
        engine.update(Gdx.graphics.deltaTime)
        gameBatch.end()

        // gameBoardView.render()

        updateAbilityCardInventoryView()
        gameUIViewport.apply()
        gameUIView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        gameViewport.update(width, height, true)
        gameUIViewport.update(width, height, false)
        gameBoardView.resize(width, height)
        gameUIView.resize(width, height)
    }

    override fun dispose() {
        gameBoardView.dispose()
        gameUIView.dispose()
        gameBatch.dispose()
        engine.removeAllEntities()
        unloadAssets()
        unsubscribeFromGameUpdates(this.toString())
        if (Game.isInGame() && !navigatingToEndGame) {
            runBlocking {
                launch {
                    try {
                        Game.leaveGame()
                        Lobby.leaveLobby()
                    } catch (e: Exception) {
                        error("Non fatal error: Problem with calling leaveGame(). Error: " + e.message)
                    }
                }
            }
        }
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
        AbilityType.entries.forEach { ability ->
            val abilityName = ability.name.lowercase()
            val filename = "abilities/cards/${abilityName}Card.png"
            if (assetManager.isLoaded(filename)) {
                assetManager.unload(filename)
            }
        }
    }

    private fun updateAbilityCardInventoryView() {
        // Update if abilityPickPrompt should be showing or not.
        val allAbilityCards = EcsEngine.getEntitiesFor(Family.all(AbilityCardComponent::class.java).get())
        val abilityCardsNotInInventory =
            allAbilityCards.filter {
                !AbilityCardComponent.mapper.get(it).isInInventory
            }

        if (abilityCardsNotInInventory.isNotEmpty()) {
            val abilityCards =
                abilityCardsNotInInventory.associateBy {
                    GameUIView.AbilityCardInformation(
                        TextureRegionComponent.mapper
                            .get(it)
                            .region
                            ?.texture,
                        AbilityCardComponent.mapper.get(it).id,
                    )
                }
            gameUIView.promptPickAbility(abilityCards.keys, ::onAbilityCardClicked)
        } else {
            gameUIView.hidePromptPickAbility()
        }

        // Update if any new cards have appeared/disappeared from inventory.
        val abilityCardsInInventory =
            allAbilityCards.filter {
                AbilityCardComponent.mapper.get(it).isInInventory
            }

        val abilityCards =
            abilityCardsInInventory.associateBy {
                GameUIView.AbilityCardInformation(
                    TextureRegionComponent.mapper
                        .get(it)
                        .region
                        ?.texture,
                    AbilityCardComponent.mapper.get(it).id,
                )
            }

        gameUIView.updateCardsInInventory(abilityCards.keys, ::onAbilityCardClicked)

        // Update if a card has been selected or deselected.
        val selectedAbilityCardEntity =
            EcsEngine.getEntitiesFor(Family.all(AbilityCardComponent::class.java, SelectionComponent::class.java).get()).firstOrNull()
        if (selectedAbilityCardEntity != null) {
            gameUIView.selectCardFromInventory(
                AbilityComponent.mapper
                    .get(selectedAbilityCardEntity)
                    .ability
                    .abilityDescription,
                AbilityCardComponent.mapper.get(selectedAbilityCardEntity).id,
            )
        } else {
            gameUIView.selectCardFromInventory("", -1)
        }
    }

    fun onAbilityCardClicked(idOfAbilityClicked: Int) {
        inputService.clickAbilityCardWithId(idOfAbilityClicked)
    }

    fun onSelectAbilityCardButtonClicked() {
        inputService.confirmAbilityChoice()
    }

    override fun setInputProcessor() {
        gameBoardView.setInputProcessor()
    }

    private fun onGameStateUpdate(gameDto: io.github.chessevolved.dtos.GameDto) {
        val pieces = gameDto.pieces
        val boardSquares = gameDto.boardSquares

        if (SupabaseGameHandler.sendingGameState) {
            SupabaseGameHandler.sendingGameState = false
            return
        }

        EcsEntityMapper.applyStateToEngine(engine, pieceFactory, gameStage, pieces, boardSquares)
    }

    private fun onTurnComplete() {
        runBlocking {
            launch {
                val (pieces, boardSquares) = EcsEntityMapper.extractStateFromEngine(engine)

                SupabaseGameHandler.sendingGameState = true

                Game.updateGameState(
                    Lobby.getLobbyId()!!,
                    pieces,
                    boardSquares,
                )
            }
        }
    }
}
