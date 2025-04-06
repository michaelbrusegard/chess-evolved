package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.entities.BoardSquareFactory
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.singletons.ECSEngine
import io.github.chessevolved.systems.BoardRenderingSystem
import io.github.chessevolved.views.GameView

class GamePresenter(
    private val view: GameView,
) : IPresenter {
    private val engine = ECSEngine
    private val pieceFactory = PieceFactory(engine)
    private val boardSquareFactory = BoardSquareFactory(engine)

    private val boardSize = 8
    private val pixelSize = view.getBoardSize().toInt() / boardSize
    private var boardScreenPosX = 0
    private var boardScreenPosY = 0

    // private val supabaseLobbyHandler = SupabaseLobbyHandler
    //
    // private suspend fun onLobbyEvent(lobby: SupabaseLobbyHandler.Lobby) {
    //     println("Lobby event received: $lobby")
    // }
    //
    // private suspend fun testSupabase() {
    //     println("Testing Supabase connection")
    //     val lobbyCode = supabaseLobbyHandler.createLobby(::onLobbyEvent)
    //     println("Created lobby with code: $lobbyCode")
    // }

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

        // engine.addSystem(
        //     RenderingSystem(
        //         view.getBatch(),
        //         pixelSize,
        //         boardScreenPosX,
        //         boardScreenPosY,
        //     ),
        // )

        setupBoard()

        // Just a tiny test for now to see if the Supabase connection works
        // GlobalScope.launch { testSupabase() }
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

    override fun render() {
        view.beginBatch()
        engine.update(Gdx.graphics.deltaTime)
        view.endBatch()
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
    }

    override fun setInputProcessor() {
        view.setInputProcessor()
    }
}
