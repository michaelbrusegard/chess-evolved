package io.github.chessevolved.singletons


enum class Piece {
    WHITE_PAWN, WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT,
    BLACK_PAWN, BLACK_KING, BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT
}

enum class Ability {
    ABILITY1, ABILITY2, ABILITY3
}

enum class WeatherEvent {
    WE1, WE2, WE3, WE4
}

data class Position(val x: Int, val y: Int)

data class PieceState(
    val piece: Piece,
    var position: Position,
    val abilities: MutableList<Ability>
)

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
