package io.github.chessevolved.systems

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import io.github.chessevolved.components.CanBeCapturedComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.data.MovementPattern
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.MoveType
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.singletons.EcsEngine

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
                val availablePositionsInDirection =
                    validateDirection(
                        movementPattern,
                        boardSize,
                        position,
                        direction,
                        playerColor,
                    )

                if (availablePositionsInDirection.isNotEmpty()) {
                    availableMoves += availablePositionsInDirection
                }
            }
        }

        return availableMoves
    }

    private fun validateDirection(
        movementPattern: MovementPattern,
        boardSize: Int,
        position: Position,
        direction: Vector2,
        playerColor: PlayerColor,
    ): MutableList<Position> {
        val availablePositionsInDirection: MutableList<Position> = ArrayList()

        val maxSteps = if (movementPattern.maxSteps == 0) boardSize else movementPattern.maxSteps

        for (step in 1..maxSteps) {
            val newX = position.x + (direction.x * step).toInt()
            val newY = position.y + (direction.y * step).toInt()

            // Check if position is outside the board
            if (newX < 0 ||
                newX >= boardSize ||
                newY < 0 ||
                newY >= boardSize
            ) {
                break
            }

            val newPosition = Position(newX, newY)

            // Check if there's a piece at the new position
            val piece =
                EcsEngine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get()).find { entity ->
                    val pos = PositionComponent.mapper.get(entity).position
                    pos == newPosition
                }

            if (piece != null) {
                val pieceColor = PlayerColorComponent.mapper.get(piece).color

                // If same color piece, we cannot move here or beyond
                if (pieceColor == playerColor) {
                    break
                } else {
                    // Found an opponent's piece - can capture but not move beyond
                    when (movementPattern.moveType) {
                        MoveType.NORMAL,
                        MoveType.CAPTURE_ONLY,
                        -> {
                            if (!movementPattern.canJump &&
                                !walkToPosition(
                                    Position(newX, newY),
                                    Position(newX - direction.x.toInt(), newY - direction.y.toInt()),
                                )
                            ) {
                                break
                            }

                            piece.add(CanBeCapturedComponent())
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
                    MoveType.NORMAL,
                    MoveType.MOVE_ONLY,
                    -> {
                        if (!movementPattern.canJump &&
                            !walkToPosition(
                                Position(newX, newY),
                                Position(newX - direction.x.toInt(), newY - direction.y.toInt()),
                            )
                        ) {
                            break
                        }

                        availablePositionsInDirection.add(newPosition)
                    }
                    else -> {} // For CAPTURE_ONLY, don't add empty positions
                }
            }
        }

        return availablePositionsInDirection
    }

    private fun walkToPosition(
        position: Position,
        startPosition: Position,
    ): Boolean {
        val xIncrementer =
            if (startPosition.x == position.x) {
                0
            } else if (position.x < startPosition.x) {
                -1
            } else {
                1
            }

        val yIncrementer =
            if (startPosition.y == position.y) {
                0
            } else if (position.y < startPosition.y) {
                -1
            } else {
                1
            }

        var newPosition = Position(startPosition.x, startPosition.y)

        while (newPosition.x != position.x || newPosition.y != position.y) {
            newPosition = Position(newPosition.x + xIncrementer, newPosition.y + yIncrementer)

            // Break out, since the normal validation takes care of this edge case.
            if (newPosition == position) {
                break
            }

            val piece =
                EcsEngine.getEntitiesFor(Family.all(PieceTypeComponent::class.java).get()).find { entity ->
                    val pos = PositionComponent.mapper.get(entity).position
                    pos == newPosition
                }
            if (piece != null) {
                return false
            }
        }
        return true
    }
}
