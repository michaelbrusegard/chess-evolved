package io.github.chessevolved.systems

import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.entities.ChessPiece

class MovementSystem {
    //TODO: refactor when the ComponentMapper class is created
    fun moveChessPiece(chesspiece: ChessPiece, x:Int, y:Int) {
        val pm:ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
        val position:PositionComponent = pm.get(chesspiece)
        // 1 tile has a size of 32 by 32 pixels
        position.changePosition(x, y)
    }
}
