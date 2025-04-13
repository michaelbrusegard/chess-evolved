package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

enum class AbilityType {
    SHIELD,
    EXPLOSION,
    SWAP,
    MIRROR,
    NEW_MOVEMENT,
}

class AbilityComponent (
    private var ability: String // switch to List<AbilityType> should we decide that a piece may have several abilities
) : Component {
    /**
     * @param ability - ability name as a string in lowercase
     */
    fun setAbility(
        ability: String
    ) {
        this.ability = ability
    }
}
