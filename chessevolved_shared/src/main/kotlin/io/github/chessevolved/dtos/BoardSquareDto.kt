package io.github.chessevolved.dtos

import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.WeatherEvent
import kotlinx.serialization.Serializable

@Serializable
data class BoardSquareDto(
    val position: Position,
    val weather: WeatherEvent,
)
