package io.github.chessevolved.systems

import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.singletons.Mappers

class MovementSystem {
    fun moveChessPiece(chesspiece: ChessPiece, x:Int, y:Int) {
        val position:PositionComponent = Mappers.getPosition(chesspiece)
        // 1 tile has a size of 32 by 32 pixels
        position.changePosition(x, y)
    }
}
