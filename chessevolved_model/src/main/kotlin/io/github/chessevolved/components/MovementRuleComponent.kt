package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.math.Vector2

class MovementRuleComponent() : Component {
    companion object {
        val mapper: ComponentMapper<MovementRuleComponent> =
                ComponentMapper.getFor(MovementRuleComponent::class.java)
    }

    /**
     * Movement rule data class
     * @param moveName The name of the movement.
     * @param directions A list containing all directions this piece can move relative to itself.
     * @param maxSteps How many steps it can take for this move, 0 is infinite
     * @param canJump If it can ignore pieces that are in its path.
     * @param isDefaultMovement If this move is for normal movement. Purpose is if it can be combined with kill.
     * @param isKill if this move is for killing purposes only.
     */
    data class MovementPattern(
        val moveName: String,
        val directions: List<Vector2>,
        val maxSteps: Int = 0,
        val canJump: Boolean = false,
        val moveType: MoveType = MoveType.NORMAL
    )

    enum class MoveType {
        NORMAL, // Both capture and move
        MOVE_ONLY, // Move only
        CAPTURE_ONLY // Capture only
    }

    private val patterns = mutableListOf<MovementPattern>()

    /**
     * Getter for the movementRule moves in the component.
     * @return Returns an ArrayList of movementRules.
     */
    fun getMovementRules(): List<MovementPattern> {
        return patterns.toList()
    }

    /**
     * Adds a movement rule to the components movementRule list.
     * @param directions ArrayList of all possible directions defined relative.
     * @param maxSteps Int Maximum amount of steps this piece can move with this rule. 0 is infinite.
     * @param canJump Boolean if movementRule can jump over other pieces in its path.
     * @param isDefaultMovement Boolean if movementRule is for movement. *Implicit, but necessary*
     * @param isKill Boolean if movementRule is for eliminating pieces.
     */
    fun addPattern(pattern: MovementPattern) {
        patterns.add(pattern)
    }

    /**
     * Remove a movementRule by its moveName.
     * @param moveName The moveName to consider removing.
     */
    fun removeMovementPattern(moveName: String) {
        var filteredMoves = patterns.stream().filter { move -> move.moveName == moveName }.toArray()
        if (filteredMoves.isEmpty()) {
            throw IllegalArgumentException("Move list does not have moveName: $moveName")
        }

        patterns.removeIf { move -> move.moveName == moveName }
    }
}
