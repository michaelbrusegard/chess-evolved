package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.serialization.GameStateSerializer
import io.github.chessevolved.singletons.ComponentMappers

class MoveValidator {
    fun checkAvailablePositions(
        playerColor: PlayerColor,
        position: Position,
        movementRuleComponent: MovementRuleComponent,
        boardSize: Int,
    ): MutableList<Position> {
        val availableMoves: MutableList<Position> = ArrayList()

        for (movementPattern in movementRuleComponent.getMovementRules()) {
            for (direction in movementPattern.directions) {
                val availablePositionsInDirection = validateDirection(
                    movementPattern,
                    boardSize,
                    position,
                    direction,
                    playerColor
                )

                if (availablePositionsInDirection.isNotEmpty()) {
                    availableMoves += availablePositionsInDirection
                }
            }
        }

        return availableMoves
    }

    private fun validateDirection(
        movementPattern: MovementRuleComponent.MovementPattern,
        boardSize: Int,
        position: Position,
        direction: Vector2,
        playerColor: PlayerColor
    ) : MutableList<Position> {
        val availablePositionsInDirection: MutableList<Position> = ArrayList()

        val maxSteps = if (movementPattern.maxSteps == 0) boardSize else movementPattern.maxSteps

        for (step in 1..maxSteps) {
            val newX = position.x + (direction.x * step).toInt()
            val newY = position.y + (direction.y * step).toInt()

            // Check if position is outside the board
            if (newX < 0 || newX >= boardSize ||
                newY < 0 || newY >= boardSize) {
                break
            }

            val newPosition = Position(newX, newY)

            // Check if there's a piece at the new position
            val piece = GameStateSerializer.getPieceEntities().find { entity ->
                val pos = ComponentMappers.posMap.get(entity).position
                pos == newPosition
            }

            if (piece != null) {
                val pieceColor = ComponentMappers.colorMap.get(piece).color

                // If same color piece, we cannot move here or beyond
                if (pieceColor == playerColor) {
                    break
                } else {
                    // Found an opponent's piece - can capture but not move beyond
                    when (movementPattern.moveType) {
                        MovementRuleComponent.MoveType.NORMAL,
                        MovementRuleComponent.MoveType.CAPTURE_ONLY -> {
                            availablePositionsInDirection.add(newPosition)
                        }
                        else -> {} // For MOVE_ONLY, don't add capture positions
                    }

                    // If we can't jump, we need to stop after finding a piece
                    if (!movementPattern.canJump) {
                        break
                    }
                }
            } else {
                // Empty position
                when (movementPattern.moveType) {
                    MovementRuleComponent.MoveType.NORMAL,
                    MovementRuleComponent.MoveType.MOVE_ONLY -> {
                        availablePositionsInDirection.add(newPosition)
                    }
                    else -> {} // For CAPTURE_ONLY, don't add empty positions
                }
            }
        }

        return availablePositionsInDirection
    }
}
