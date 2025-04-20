package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

enum class AbilityType(
    val abilityDescription: String,
) {
    SHIELD("Blocks attacks from opponent pieces."),
    EXPLOSION("Causes an explosion around the tile that your piece moves to."),
    SWAP("Swaps position with a piece you take?"),
    MIRROR("Mirrors an ability used against this piece."),
    NEW_MOVEMENT("No clue what this does lol..."),
}

class AbilityComponent(
    var abilities: List<AbilityType>,
) : Component {
    companion object {
        val mapper: ComponentMapper<AbilityComponent> =
            ComponentMapper.getFor(AbilityComponent::class.java)
    }
}
