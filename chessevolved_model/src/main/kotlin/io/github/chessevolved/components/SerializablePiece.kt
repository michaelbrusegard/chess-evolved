package io.github.chessevolved.components
import kotlinx.serialization.Serializable

@Serializable
data class SerializablePiece(
    val position: Position,
    val type: PieceType,
    val color: PlayerColor,
)
