package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.components.WeatherEventComponent

class BoardSquareFactory(
    private val engine: Engine,
) {
    // We do not need to create a board square for every square, only the ones affected by a weather event
    fun createBoardSquare(
        position: Position,
        weatherEvent: WeatherEvent,
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(WeatherEventComponent(weatherEvent))
            engine.addEntity(this)
        }
}
