package io.github.chessevolved.components
import kotlinx.serialization.Serializable

@Serializable
data class SerializableBoardSquare(
    val position: Position,
    val weather: WeatherEvent,
)
