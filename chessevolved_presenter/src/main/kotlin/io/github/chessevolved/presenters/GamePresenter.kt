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
import io.github.chessevolved.components.AbilityType
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.entities.AbilityItemFactory
import io.github.chessevolved.entities.BoardSquareFactory
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.singletons.ECSEngine
import io.github.chessevolved.systems.CaptureSystem
import io.github.chessevolved.systems.InputService
import io.github.chessevolved.systems.InputSystem
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
    private val selectionListener: SelectionEntityListener
    private val captureSystem: CaptureSystem
    private val inputSystem: InputSystem
    private val inputService: InputService = InputService()

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

        inputSystem = InputSystem()
        engine.addSystem(inputSystem)

        loadRequiredAssets()
        assetManager.finishLoading()

        setupBoard()

        // If we do not call this the board will not be displayed
        resize(Gdx.graphics.width, Gdx.graphics.height)

        // TODO: Remove after testing ability card UI
        val testAbilityCard = abilityItemFactory.createAbilityItem(AbilityType.EXPLOSION)
        AbilityCardComponent.mapper.get(testAbilityCard).isInInventory = true
        val testAbilityCard2 = abilityItemFactory.createAbilityItem(AbilityType.EXPLOSION)
        val testAbilityCard3 = abilityItemFactory.createAbilityItem(AbilityType.SWAP)
        val testAbilityCard4 = abilityItemFactory.createAbilityItem(AbilityType.NEW_MOVEMENT)
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

        AbilityType.entries.forEach { ability ->
            val abilityName = ability.name.lowercase()
            assetManager.load("abilities/$abilityName-card.png", Texture::class.java)
        }
    }

    private fun setupGameView() {
        // TODO: Pass in if the player is the white player or not.
        gameUIView = GameUIView(gameUIViewport, true, ::onSelectAbilityCardButtonClicked)
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
                ) { clickedPosition -> inputService.clickBoardSquareAtPosition(clickedPosition) }
            }
        }

        val startX: Int = (boardWorldSize / 2) - 4
        for (startPos in startX until startX + 8) {
            pieceFactory.createPawn(
                true,
                Position(startPos, 1),
                PlayerColor.WHITE,
                gameStage,
            ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }

            pieceFactory.createPawn(
                false,
                Position(startPos, boardWorldSize - 2),
                PlayerColor.BLACK,
                gameStage,
            ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }

            when (startPos) {
                startX -> {
                    for (j in listOf(0, 7)) {
                        pieceFactory.createRook(
                            Position(startX + j, 0),
                            PlayerColor.WHITE,
                            gameStage,
                        ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }

                        pieceFactory.createRook(
                            Position(startX + j, boardWorldSize - 1),
                            PlayerColor.BLACK,
                            gameStage,
                        ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }
                    }
                }
                startX + 1 -> {
                    for (j in listOf(1, 6)) {
                        pieceFactory.createKnight(
                            Position(startX + j, 0),
                            PlayerColor.WHITE,
                            gameStage,
                        ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }

                        pieceFactory.createKnight(
                            Position(startX + j, boardWorldSize - 1),
                            PlayerColor.BLACK,
                            gameStage,
                        ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }
                    }
                }
                startX + 2 -> {
                    for (j in listOf(2, 5)) {
                        pieceFactory.createBishop(
                            Position(startX + j, 0),
                            PlayerColor.WHITE,
                            gameStage,
                        ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }

                        pieceFactory.createBishop(
                            Position(startX + j, boardWorldSize - 1),
                            PlayerColor.BLACK,
                            gameStage,
                        ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }
                    }
                }
                startX + 3 -> {
                    pieceFactory.createQueen(
                        Position(startPos, 0),
                        PlayerColor.WHITE,
                        gameStage,
                    ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }

                    pieceFactory.createQueen(
                        Position(startPos, boardWorldSize - 1),
                        PlayerColor.BLACK,
                        gameStage,
                    ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }
                }
                startX + 4 -> {
                    pieceFactory.createKing(
                        Position(startPos, 0),
                        PlayerColor.WHITE,
                        gameStage,
                    ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }

                    pieceFactory.createKing(
                        Position(startPos, boardWorldSize - 1),
                        PlayerColor.BLACK,
                        gameStage,
                    ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) }
                }
                else -> {}
            }
        }
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
            val filename = "abilities/$abilityName-card.png"
            if (assetManager.isLoaded(filename)) {
                assetManager.unload(filename)
            }
        }
    }

    private fun updateAbilityCardInventoryView() {
        // Update if abilityPickPrompt should be showing or not.
        val allAbilityCards = ECSEngine.getEntitiesFor(Family.all(AbilityCardComponent::class.java).get())
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
            ECSEngine.getEntitiesFor(Family.all(AbilityCardComponent::class.java, SelectionComponent::class.java).get()).firstOrNull()
        if (selectedAbilityCardEntity != null) {
            gameUIView.selectCardFromInventory(
                AbilityComponent.mapper
                    .get(selectedAbilityCardEntity)
                    .abilities[0]
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
}
