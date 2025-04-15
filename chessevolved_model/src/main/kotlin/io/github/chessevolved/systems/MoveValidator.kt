package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.singletons.ComponentMappers
import io.github.chessevolved.singletons.EntityFamilies

class MoveValidator {
    fun movePieceToPos(
        piece: Entity?,
        position: Position,
        availableMoves: MutableList<Position>,
    ) {
        if (piece == null) {
            throw NullPointerException("Piece is null")
        }

        val availableMovesSet = availableMoves.toSet()

        if (!availableMovesSet.contains(position)) {
            throw IllegalArgumentException("Position is not available")
        }

        val piecePositionComponent = ComponentMappers.posMap.get(piece)
        val actorComponent = ComponentMappers.actorMap.get(piece)

        piecePositionComponent.position = position
        actorComponent.actor.setPosition(position.x.toFloat(), position.y.toFloat())
    }

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

            if (newX < 0 || newX >= boardSize ||
                newY < 0 || newY >= boardSize) {
                break
            }


            if (!movementPattern.canJump) {
                for (y in position.y..newY) {
                    for (x in position.x..newX) {
                        validatePosition(Position(x, y), playerColor, availablePositionsInDirection)
                    }
                }
            } else {
                validatePosition(Position(newX, newY), playerColor, availablePositionsInDirection)
            }
        }

        return availablePositionsInDirection
    }

    private fun validatePosition(
        position: Position,
        playerColor: PlayerColor,
        positionList: MutableList<Position>
    ) {
        val piece =
            EntityFamilies.getPieceEntities().find { entity ->
                val pos = ComponentMappers.posMap.get(entity).position
                pos == position
            }

        if (piece != null) {
            val pieceColor = ComponentMappers.colorMap.get(piece).color
            if (pieceColor == playerColor) {
                return
            } else {
                // Only case here is that the piece is of opposite color.
                // Perhaps add red square TODO
                positionList.add(position)
                return
            }
        } else {
            positionList.add(position)
            return
        }
    }
}
