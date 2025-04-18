package io.github.chessevolved.dtos

import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import kotlinx.serialization.Serializable

@Serializable
data class PieceDto(
    val position: Position,
    val type: PieceType,
    val color: PlayerColor,
)
