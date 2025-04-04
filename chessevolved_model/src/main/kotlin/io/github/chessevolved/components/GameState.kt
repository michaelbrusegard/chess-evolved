package io.github.chessevolved.components
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val pieces: List<SerializablePiece>,
    val boardSquares: List<SerializableBoardSquare>,
)
