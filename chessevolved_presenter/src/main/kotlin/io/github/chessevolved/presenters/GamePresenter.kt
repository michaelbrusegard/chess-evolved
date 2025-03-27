package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.chessevolved.components.BoardSizeComponent
import io.github.chessevolved.components.ChessBoardSpriteComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SpriteComponent
import io.github.chessevolved.entities.ChessBoard
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler.joinGame
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler.updateGameState
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler.startGame
import io.github.chessevolved.views.AndroidView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GamePresenter(
    givenView: AndroidView,
) : IPresenter {
    val pieces: MutableList<ChessPiece> = mutableListOf()
    val board: ChessBoard = ChessBoard()

    val boardViewportSize: Float = Gdx.graphics.width - 10f
    var boardScreenPosX: Int = 0
    var boardScreenPosY: Int = 0
    val pixelSize: Int = 32
    val view: AndroidView = givenView

    // Temporary value, should be defined elsewhere
    val boardSize: Int = 8

    // Testing methods for supabase. Just to provide an example
    val supabaseLobbyHandler = SupabaseLobbyHandler

    private fun onGameEvent(newGameRow: SupabaseGameHandler.Game) {
        println("Game was updated! $newGameRow")
    }

    private suspend fun onLobbyEvent(newLobbyRow: SupabaseLobbyHandler.Lobby) {
        println("Registered lobby event in lobby event handler! $newLobbyRow")
        if (newLobbyRow.game_started) {
            joinGame(newLobbyRow.lobby_code, ::onGameEvent)
        }
    }

    private suspend fun testSupabase() {
        println("Testing")
        val lobbyCode = supabaseLobbyHandler.createLobby(::onLobbyEvent)

        Thread.sleep(3000L)
        startGame(lobbyCode, arrayOf<String>())
        Thread.sleep(3000L)

        updateGameState(lobbyCode, arrayOf<String>(), arrayOf<String>(), SupabaseGameHandler.TurnColor.BLACK, "b3")
        // leaveLobby(lobbyCode)
    }
    // END testing for supabase

    init {
        GlobalScope.launch { testSupabase() }
        board.add(BoardSizeComponent(boardSize))
        board.add(ChessBoardSpriteComponent())

        val piece: ChessPiece = ChessPiece()
        piece.add(PositionComponent(4, 4))
        piece.add(SpriteComponent("pieces/rookBlackExample.png"))

        pieces.add(piece)

        boardScreenPosX = (Gdx.graphics.width - (boardSize * pixelSize)) / 2
        boardScreenPosY = (Gdx.graphics.height - (boardSize * pixelSize)) / 2
    }

    override fun render() {
        view.beginBatch()
        for (y in 0..boardSize - 1) {
            for (x in 0..boardSize - 1) {
                var sprite = board.getComponent(ChessBoardSpriteComponent::class.java).whiteTileSprite
                if ((y + x) % 2 == 0) {
                    sprite = board.getComponent(ChessBoardSpriteComponent::class.java).blackTileSprite
                }
                sprite.setPosition(boardScreenPosX.toFloat() + pixelSize * x, boardScreenPosY.toFloat() + pixelSize * y)
                view.render(sprite)
            }
        }

        val sprite: Sprite = pieces[0].getComponent(SpriteComponent::class.java).sprite
        val posComp: PositionComponent = pieces[0].getComponent(PositionComponent::class.java)
        sprite.setPosition(
            (boardScreenPosX + (posComp.xPos - 1) * pixelSize).toFloat(),
            (boardScreenPosY + (posComp.yPos - 1) * pixelSize).toFloat(),
        )
        view.render(sprite)

        view.endBatch()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
    }

    override fun dispose() {
    }

    override fun setInputProcessor() {
        TODO("Not yet implemented")
    }
}
