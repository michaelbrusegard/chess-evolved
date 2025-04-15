package io.github.chessevolved.serialization

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.GameState
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SerializableBoardSquare
import io.github.chessevolved.components.SerializablePiece
import io.github.chessevolved.components.WeatherEventComponent
import io.github.chessevolved.singletons.ECSEngine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object GameStateSerializer {
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
                    position = PositionComponent.mapper.get(entity).position,
                    type = PieceTypeComponent.mapper.get(entity).type,
                    color = PlayerColorComponent.mapper.get(entity).color,
                )
            }

        val boardSquares =
            engine.getEntitiesFor(boardSquareFamily).map { entity ->
                SerializableBoardSquare(
                    position = PositionComponent.mapper.get(entity).position,
                    weather = WeatherEventComponent.mapper.get(entity).event,
                )
            }

        val gameState = GameState(pieces = pieces, boardSquares = boardSquares)

        return Json.encodeToString(gameState)
    }
}
