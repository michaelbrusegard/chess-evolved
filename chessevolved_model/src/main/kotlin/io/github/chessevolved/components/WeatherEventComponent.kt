package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

enum class WeatherEvent {
    FOG,
    LANDSLED,
    WIND,
    NONE,
}

class WeatherEventComponent(
    var event: WeatherEvent,
) : Component {
    companion object {
        val mapper: ComponentMapper<WeatherEventComponent> =
            ComponentMapper.getFor(WeatherEventComponent::class.java)
    }
}
