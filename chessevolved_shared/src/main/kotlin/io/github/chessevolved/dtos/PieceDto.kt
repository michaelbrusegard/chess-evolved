package io.github.chessevolved.dtos

import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.AbilityType
import io.github.chessevolved.enums.PieceType
import io.github.chessevolved.enums.PlayerColor
import kotlinx.serialization.Serializable

@Serializable
data class PieceDto(
    var position: Position,
    var previousPosition: Position,
    val type: PieceType,
    val color: PlayerColor,
    var abilityType: AbilityType?,
    var abilityCurrentCooldown: Int = 0,
)
