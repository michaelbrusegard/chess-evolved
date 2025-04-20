package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.data.Position

class ValidMovesComponent(
    var validMoves: List<Position>,
) : Component {
    companion object {
        val mapper: ComponentMapper<ValidMovesComponent> =
            ComponentMapper.getFor(ValidMovesComponent::class.java)
    }
}
