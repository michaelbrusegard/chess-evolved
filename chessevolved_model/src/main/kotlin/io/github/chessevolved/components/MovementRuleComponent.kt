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
     * @param moveType Enum for which type of movement it is.
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
