package io.github.chessevolved.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LobbyDto(
    val id: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("lobby_code")
    val lobbyCode: String,
    @SerialName("second_player")
    val secondPlayer: Boolean,
    @SerialName("game_started")
    val gameStarted: Boolean,
    val settings: SettingsDto,
)
