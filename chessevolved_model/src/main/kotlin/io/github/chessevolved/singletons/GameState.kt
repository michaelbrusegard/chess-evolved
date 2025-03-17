package io.github.chessevolved.singletons
import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
enum class Piece {
    WHITE_PAWN, WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT,
    BLACK_PAWN, BLACK_KING, BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT
}

@Serializable
enum class Ability {
    ABILITY1, ABILITY2, ABILITY3
}

@Serializable
enum class WeatherEvent {
    WE1, WE2, WE3, WE4
}

@Serializable
data class Position(val x: Int, val y: Int)

@Serializable
data class PieceState(
    val piece: Piece,
    var position: Position,
    val abilities: MutableList<Ability>
)

@Serializable
data class TileState(
    val position: Position,
    val weatherEvents: MutableList<WeatherEvent> = mutableListOf()
)

object GameState {
    val pieces: MutableList<PieceState> = mutableListOf()
    val board: Array<Array<TileState>> = Array(8) { x ->
        Array(8) { y ->
            TileState(Position(x, y))
        }
    }

    fun serialize(): String {
        val snapshot = GameStateSnapshot(pieces, board.flatten())
        return Json.encodeToString(snapshot)
    }

    fun deserialize(json: String) {
        val snapshot = Json.decodeFromString<GameStateSnapshot>(json)
        pieces.clear()
        pieces.addAll(snapshot.pieces)

        snapshot.boardTiles.forEach { tile ->
            board[tile.position.x][tile.position.y] = tile
        }
    }

    @Serializable
    private data class GameStateSnapshot(
        val pieces: List<PieceState>,
        val boardTiles: List<TileState>
    )
}
/*
object GameState {
    private var chessBoard = Array(8) { Array<Piece?>(8) { null } }

    fun getChessBoard(): Array<Array<Piece?>> {
        return chessBoard
    }

    fun setChessBoard(newBoard: Array<Array<Piece?>>) {
        chessBoard = newBoard
    }

    fun updateChessBoard(updatedBoard: Array<Array<Piece?>>) {
        chessBoard = updatedBoard
    }

}
*/
