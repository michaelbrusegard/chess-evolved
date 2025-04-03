package io.github.chessevolved.systems

import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.singletons.Mappers

class AbilitySystem {
    fun addAbilityToChessPiece(
        chesspiece: ChessPiece,
        type: Int,
        ability: String,
        description: String
    ) {
        val abilities: AbilityComponent = Mappers.getAbilities(chesspiece)
        abilities.addAbility(type, ability, description)
    }

    // TODO : abilities need to its own entity, and need to interact with it, and add attributes logic etc to it
}
