package io.github.chessevolved.systems

import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.singletons.Mappers

class AbilitySystem {
    fun addAbilityToChessPiece(
        chesspiece: ChessPiece,
        type: Int,
        ability: String
    ) {
        val abilities: AbilityComponent = Mappers.getAbilities(chesspiece)
        abilities.addAbility(type, ability)
    }
}
