package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

/**
 * Movement rule data class
 * @param moveName The name of the movement.
 * @param directions A list containing all directions this piece can move relative to itself.
 * @param maxSteps How many steps it can take for this move, 0 is infinite
 * @param canJump If it can ignore pieces that are in its path.
 * @param isDefaultMovement If this move is for normal movement. Purpose is if it can be combined with kill.
 * @param isKill if this move is for killing purposes only.
 */
data class MovementRule(
    val moveName: String,
    val directions: ArrayList<Vector2>,
    val maxSteps: Int,
    val canJump: Boolean,
    val isDefaultMovement: Boolean,
    val isKill: Boolean,
)

class MovementRuleComponent() : Component {
    private val moves = ArrayList<MovementRule>()

    /**
     * Getter for the movementRule moves in the component.
     * @return Returns an ArrayList of movementRules.
     */
    fun getMovementRules(): ArrayList<MovementRule> {
        return ArrayList(moves)
    }

    /**
     * Adds a movement rule to the components movementRule list.
     * @param directions ArrayList of all possible directions defined relative.
     * @param maxSteps Int Maximum amount of steps this piece can move with this rule. 0 is infinite.
     * @param canJump Boolean if movementRule can jump over other pieces in its path.
     * @param isDefaultMovement Boolean if movementRule is for movement. *Implicit, but necessary*
     * @param isKill Boolean if movementRule is for eliminating pieces.
     */
    fun addMovementRule(
        moveName: String,
        directions: ArrayList<Vector2>,
        maxSteps: Int,
        canJump: Boolean,
        isDefaultMovement: Boolean,
        isKill: Boolean,
    ) {
        var movementRule =
            MovementRule(
                moveName = moveName,
                directions = directions,
                maxSteps = maxSteps,
                canJump = canJump,
                isDefaultMovement = isDefaultMovement,
                isKill = isKill,
            )

        moves.add(movementRule)
    }

    /**
     * Remove a movementRule by its moveName.
     * @param moveName The moveName to consider removing.
     */
    fun removeMovementRule(moveName: String) {
        var filteredMoves = moves.stream().filter { move -> move.moveName == moveName }.toArray()
        if (filteredMoves.isEmpty()) {
            throw IllegalArgumentException("Move list does not have moveName: $moveName")
        }

        moves.removeIf { move -> move.moveName == moveName }
    }
}
