package io.github.chessevolved.dtos

import io.github.chessevolved.components.Position
import io.github.chessevolved.components.WeatherEvent
import kotlinx.serialization.Serializable

@Serializable
data class BoardSquareDto(
    val position: Position,
    val weather: WeatherEvent,
)
