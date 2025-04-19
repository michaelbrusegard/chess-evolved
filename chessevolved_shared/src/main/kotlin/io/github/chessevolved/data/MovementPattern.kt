package io.github.chessevolved.data

import com.badlogic.gdx.math.Vector2
import io.github.chessevolved.enums.MoveType

/**
 * Movement rule data class
 * @param moveName The name of the movement.
 * @param directions A list containing all directions this piece can move relative to itself.
 * @param maxSteps How many steps it can take for this move, 0 is infinite
 * @param canJump If it can ignore pieces that are in its path.
 * @param isFirstMove Checks if this specific move is only meant as a first move. Validator will remove this move.
 * @param moveType Enum for which type of movement it is.
 */
data class MovementPattern(
    val moveName: String,
    val directions: List<Vector2>,
    val maxSteps: Int = 0,
    val canJump: Boolean = false,
    val isFirstMove: Boolean = false,
    val moveType: MoveType = MoveType.NORMAL,
)
