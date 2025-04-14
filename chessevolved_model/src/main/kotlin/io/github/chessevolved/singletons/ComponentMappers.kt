package io.github.chessevolved.singletons

import com.badlogic.ashley.core.ComponentMapper
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.WeatherEventComponent

object ComponentMappers {
    var posMap: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
        private set
    var typeMap: ComponentMapper<PieceTypeComponent> = ComponentMapper.getFor(PieceTypeComponent::class.java)
        private set
    var colorMap: ComponentMapper<PlayerColorComponent> = ComponentMapper.getFor(PlayerColorComponent::class.java)
        private set
    var weatherMap: ComponentMapper<WeatherEventComponent> = ComponentMapper.getFor(WeatherEventComponent::class.java)
        private set
    var movementMap: ComponentMapper<MovementRuleComponent> = ComponentMapper.getFor(MovementRuleComponent::class.java)
        private set

}
