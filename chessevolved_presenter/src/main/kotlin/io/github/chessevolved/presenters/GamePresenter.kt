package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.github.chessevolved.Navigator
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.entities.BoardSquareFactory
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.singletons.ECSEngine
import com.badlogic.gdx.graphics.Texture
import io.github.chessevolved.systems.RenderingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.views.GameView

class GamePresenter(
    private val view: GameView,
    private val navigator: Navigator,
    private val assetManager: AssetManager,
) : IPresenter {
    private val engine = ECSEngine
    private val pieceFactory = PieceFactory(engine, assetManager)
    private val boardSquareFactory = BoardSquareFactory(engine)

    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(8f, 8f, gameCamera)
    private val gameBatch: SpriteBatch

    private var RenderingSystem: RenderingSystem

    private val boardWorldSize = 8f

    private lateinit var blackTileRegion: TextureRegion
    private lateinit var whiteTileRegion: TextureRegion

    init {
        view.init()
        gameBatch = view.getGameBatch()

        gameCamera.position.set(boardWorldSize / 2f, boardWorldSize / 2f, 0f)
        gameCamera.update()

renderingSystem = RenderingSystem(gameBatch, gameCamera)

        loadRequiredAssets()
        assetManager.finishLoading()
        assignLoadedRegions()

        setupBoard()
    }

    private fun loadRequiredAssets() {
        assetManager.load("board/black-tile.png", Texture::class.java)
        assetManager.load("board/white-tile.png", Texture::class.java)

        // When we assets for all pieces we should comment out the code below
        assetManager.load("pieces/black-rook.png", Texture::class.java)
        // PlayerColor.entries.forEach { color ->
        //     PieceType.entries.forEach { type ->
        //         val colorStr = color.name.lowercase()
        //         val typeStr = type.name.lowercase()
        //         assetManager.load("pieces/${colorStr}-${typeStr}.png", Texture::class.java)
        //     }
        // }
    }

    private fun assignLoadedRegions() {
        blackTileRegion =
            TextureRegion(assetManager.get("board/black-tile.png", Texture::class.java))
        whiteTileRegion =
            TextureRegion(assetManager.get("board/white-tile.png", Texture::class.java))
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
        renderingSystem.update(0f)
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
unloadAssets()
    }

    private fun unloadAssets() {
        if (assetManager.isLoaded("board/black-tile.png")) {
            assetManager.unload("board/black-tile.png")
        }
        if (assetManager.isLoaded("board/white-tile.png")) {
            assetManager.unload("board/white-tile.png")
        }
            PieceType.entries.forEach { type ->
                val colorStr = color.name.lowercase()
                val typeStr = type.name.lowercase()
                val filename = "pieces/${colorStr}-${typeStr}.png"
                if (assetManager.isLoaded(filename)) {
                    assetManager.unload(filename)
                }
            }
        }

    override fun setInputProcessor() {
        view.setInputProcessor()
    }
}
