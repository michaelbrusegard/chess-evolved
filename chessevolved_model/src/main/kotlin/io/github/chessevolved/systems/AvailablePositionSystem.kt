package io.github.chessevolved.systems

import com.badlogic.gdx.math.Vector2
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.singletons.ComponentMappers
import io.github.chessevolved.singletons.EntityFamilies

class AvailablePositionSystem() {
    fun checkAvailablePositions(
        playerColor: PlayerColor,
        position: Position,
        movementRuleComponent: MovementRuleComponent,
        boardSize: Int,
    ): MutableList<Position> {
        val moves =
            findAllTheMoves(
                movementRuleComponent,
                position,
                boardSize,
            )
        val availableMoves = findAllTheAvailableMoves(playerColor, moves)
        return availableMoves
    }

    private fun findAllTheMoves(
        movementRuleComponent: MovementRuleComponent,
        position: Position,
        boardSize: Int,
    ): MutableMap<Vector2, List<Position>> {
        val moves: MutableMap<Vector2, List<Position>> = mutableMapOf()
        for (movementRule in movementRuleComponent.getMovementRules()) {
            for (direction in movementRule.directions) {
                val availablePositionsInDirection = mutableListOf<Position>()

                val maxSteps = if (movementRule.maxSteps == 0) boardSize else movementRule.maxSteps

                for (step in 1..maxSteps) {
                    val newX = position.x + (direction.x * step).toInt()
                    val newY = position.y + (direction.y * step).toInt()

                    // Skip if outside the board
                    if (newX < 0 || newX >= boardSize ||
                        newY < 0 || newY >= boardSize
                    ) {
                        break
                    }

                    availablePositionsInDirection.add(Position(x = newX, y = newY))
                }

                // Only add to map if we actually have moves in that direction
                if (availablePositionsInDirection.isNotEmpty()) {
                    moves[direction] = availablePositionsInDirection
                }
            }
        }

        return moves
    }

    private fun findAllTheAvailableMoves(
        playerColor: PlayerColor,
        moves: MutableMap<Vector2, List<Position>>,
    ): MutableList<Position> {
        val availableFilteredMoves: MutableList<Position> = mutableListOf()
        for (direction in moves.keys) {
            val availablePositions = moves[direction]

            for (position in availablePositions ?: emptyList()) {
                val piece =
                    EntityFamilies.getPieceEntities().find { entity ->
                        val pos = ComponentMappers.posMap.get(entity).position
                        pos == position
                    }
                if (piece != null) {
                    val pieceColor = ComponentMappers.colorMap.get(piece).color
                    if (pieceColor == playerColor) {
                        break
                    } else {
                        // TODO if kill logic here
                        availableFilteredMoves.add(position)
                        break // If the piece is of the opposite color, we can stop checking further in this direction
                    }
                } else {
                    availableFilteredMoves.add(position)
                }
            }
        }
        return availableFilteredMoves
    }
}
