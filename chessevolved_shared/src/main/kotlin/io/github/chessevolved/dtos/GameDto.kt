package io.github.chessevolved.dtos

import kotlinx.serialization.Serializable
import io.github.chessevolved.enums.PlayerColor

@Serializable
data class GameDto(
    val id: Int,
    val updated_at: String,
    val lobby_code: String,
    val last_move: String?,
    val turn: PlayerColor,
    val pieces: List<PieceDto>,
    val board_squares: List<BoardSquareDto>,
    val player_disconnected: Boolean,
    val want_rematch: Boolean,
)
