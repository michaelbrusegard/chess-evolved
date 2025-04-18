package io.github.chessevolved.dtos

import kotlinx.serialization.Serializable

enum class TurnColor {
    WHITE,
    BLACK,
}

@Serializable
data class GameDto(
    val id: Int,
    val updated_at: String,
    val lobby_code: String,
    val last_move: String?,
    val turn: TurnColor,
    val pieces: List<PieceDto>,
    val board_squares: List<BoardSquareDto>,
    val player_disconnected: Boolean,
    val want_rematch: Boolean,
)
