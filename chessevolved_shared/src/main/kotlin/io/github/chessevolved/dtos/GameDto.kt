package io.github.chessevolved.dtos

import io.github.chessevolved.enums.PlayerColor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Int,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("lobby_code")
    val lobbyCode: String,
    val turn: PlayerColor,
    val pieces: List<PieceDto>,
    @SerialName("board_squares")
    val boardSquares: List<BoardSquareDto>,
    @SerialName("player_disconnected")
    val playerDisconnected: Boolean,
    @SerialName("want_rematch")
    val wantRematch: Boolean,
)
