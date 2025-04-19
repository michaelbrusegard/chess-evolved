package io.github.chessevolved.dtos

import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.PieceType
import io.github.chessevolved.enums.PlayerColor
import kotlinx.serialization.Serializable

@Serializable
data class PieceDto(
    val position: Position,
    val type: PieceType,
    val color: PlayerColor,
)
