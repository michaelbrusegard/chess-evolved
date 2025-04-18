package io.github.chessevolved.dtos

import io.github.chessevolved.enums.PlayerColor
import kotlinx.serialization.Serializable

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
