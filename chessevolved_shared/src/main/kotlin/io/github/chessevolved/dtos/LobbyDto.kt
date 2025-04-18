package io.github.chessevolved.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LobbyDto(
    val id: Int,
    val created_at: String,
    val lobby_code: String,
    val second_player: Boolean,
    val game_started: Boolean,
    val settings: SettingsDto,
)
