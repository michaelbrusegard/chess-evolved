package io.github.chessevolved.singletons


enum class Piece {
    WHITE_PAWN, WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT,
    BLACK_PAWN, BLACK_KING, BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT
}

object GameState {

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
