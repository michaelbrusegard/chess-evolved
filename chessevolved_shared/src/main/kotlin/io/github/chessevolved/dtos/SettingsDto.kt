package io.github.chessevolved.dtos

import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    val fogOfWar: Boolean,
    val boardSize: Int,
)
