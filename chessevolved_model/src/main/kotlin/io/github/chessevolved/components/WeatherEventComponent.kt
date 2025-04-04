package io.github.chessevolved.components

import com.badlogic.ashley.core.Component

enum class WeatherEvent {
    FOG,
    LANDSLED,
    WIND,
}

class WeatherEventComponent(
    var event: WeatherEvent,
) : Component
