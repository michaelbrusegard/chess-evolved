package io.github.chessevolved.singletons

import com.badlogic.ashley.core.Entity

object PlayerGameplayManager {
    private val player1Pieces: MutableList<Entity> = ArrayList()
    private val player2Pieces: MutableList<Entity> = ArrayList()

    fun player1AddPiece(piece: Entity) {
        if (piece == null) {
            throw NullPointerException("Piece is null")
        }
        player1Pieces.add(piece)
    }

    fun player2AddPiece(piece: Entity) {
        if (piece == null) {
            throw NullPointerException("Piece is null")
        }
        player2Pieces.add(piece)
    }
}
