package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

enum class WeatherEvent {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING,
}

class WeatherEventComponent(
    var event: WeatherEvent,
) : Component
