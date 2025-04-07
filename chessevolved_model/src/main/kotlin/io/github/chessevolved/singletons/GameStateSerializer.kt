package io.github.chessevolved.serialization

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.GameState
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SerializableBoardSquare
import io.github.chessevolved.components.SerializablePiece
import io.github.chessevolved.components.WeatherEventComponent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object GameStateSerializer {
    private val posMap: ComponentMapper<PositionComponent> =
        ComponentMapper.getFor(PositionComponent::class.java)
    private val typeMap: ComponentMapper<PieceTypeComponent> =
        ComponentMapper.getFor(PieceTypeComponent::class.java)
    private val colorMap: ComponentMapper<PlayerColorComponent> =
        ComponentMapper.getFor(PlayerColorComponent::class.java)
    private val weatherMap: ComponentMapper<WeatherEventComponent> =
        ComponentMapper.getFor(WeatherEventComponent::class.java)

    // Maybe move families out of here so they can be used for getting the components in systems too
    private val pieceFamily: Family =
        Family
            .all(
                PositionComponent::class.java,
                PieceTypeComponent::class.java,
                PlayerColorComponent::class.java,
                AbilityComponent::class.java,
            ).get()

    private val boardSquareFamily: Family =
        Family
            .all(
                PositionComponent::class.java,
                WeatherEventComponent::class.java,
            ).exclude(PieceTypeComponent::class.java)
            .get()

    fun serializeToJson(engine: Engine): String {
        val pieces =
            engine.getEntitiesFor(pieceFamily).map { entity ->
                SerializablePiece(
                    position = posMap.get(entity).position,
                    type = typeMap.get(entity).type,
                    color = colorMap.get(entity).color,
                )
            }

        val boardSquares =
            engine.getEntitiesFor(boardSquareFamily).map { entity ->
                SerializableBoardSquare(
                    position = posMap.get(entity).position,
                    weather = weatherMap.get(entity).event,
                )
            }

        val gameState = GameState(pieces = pieces, boardSquares = boardSquares)

        return Json.encodeToString(gameState)
    }
}
