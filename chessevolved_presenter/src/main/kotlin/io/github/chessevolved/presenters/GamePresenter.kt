package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.chessevolved.components.BoardSizeComponent
import io.github.chessevolved.components.ChessBoardSpriteComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SpriteComponent
import io.github.chessevolved.entities.ChessBoard
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.singletons.ECSEngine
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler.joinGame
import io.github.chessevolved.singletons.supabase.SupabaseGameHandler.updateGameBoard
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler.startGame
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GamePresenter : IPresenter {
    // Have a list of pieces for now, this should be established in the playersingleton later.
    val pieces: MutableList<ChessPiece> = mutableListOf()
    val board: ChessBoard = ChessBoard()
    val supabaseLobbyHandler = SupabaseLobbyHandler

    val boardSize: Int = 8

    private fun onGameEvent(newGameRow: SupabaseGameHandler.Game) {
        println("Game was updated! $newGameRow")
    }

    private suspend fun onLobbyEvent(newLobbyRow: SupabaseLobbyHandler.Lobby) {
        println("Registered lobby event in lobby event handler! $newLobbyRow")
        if (newLobbyRow.game_started) {
            joinGame(newLobbyRow.lobby_code, ::onGameEvent)
        }
    }

    init {

        suspend fun test() {
            val lobbyCode = supabaseLobbyHandler.createLobby(::onLobbyEvent)

            Thread.sleep(3000L)
            startGame(lobbyCode)
            Thread.sleep(3000L)

            updateGameBoard(lobbyCode, "asd")
            // leaveLobby(lobbyCode)
        }

        GlobalScope.launch { test() }
        // Test values for now
        for (i in 0..4) {
            val piece: ChessPiece = ChessPiece()
            piece.add(PositionComponent(i, i))
            piece.add(SpriteComponent("pieces/rookBlackExample.png", i * 32.0f, i * 32.0f))
            pieces.add(piece)
            ECSEngine.addEntity(piece)
        }

        board.add(BoardSizeComponent(boardSize))
        board.add(ChessBoardSpriteComponent())
        ECSEngine.addEntity(board)
    }

    fun getPieceSprites(): List<Sprite> = pieces.map { it.getComponent(SpriteComponent::class.java).sprite }

    fun getBoardSprites(): List<Sprite> =
        listOf(
            board.getComponent(ChessBoardSpriteComponent::class.java).blackTileSprite,
            board.getComponent(ChessBoardSpriteComponent::class.java).whiteTileSprite,
        )
}
