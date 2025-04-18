package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.data.MovementPattern

class MovementRuleComponent : Component {
    companion object {
        val mapper: ComponentMapper<MovementRuleComponent> =
            ComponentMapper.getFor(MovementRuleComponent::class.java)
    }

    private val patterns = mutableListOf<MovementPattern>()

    /**
     * Getter for the movementRule moves in the component.
     * @return Returns an ArrayList of movementRules.
     */
    fun getMovementRules(): List<MovementPattern> = patterns.toList()

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
