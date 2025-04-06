package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

class AbilityComponent (
    private var ability: String
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
