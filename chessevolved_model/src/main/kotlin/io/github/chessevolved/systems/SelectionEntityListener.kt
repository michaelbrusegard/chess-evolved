package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import io.github.chessevolved.components.AbilityCardComponent
import io.github.chessevolved.components.CanBeCapturedComponent
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.ValidMovesComponent
import io.github.chessevolved.components.WeatherEventComponent
import io.github.chessevolved.singletons.EcsEngine
import io.github.chessevolved.singletons.GameSettings

class SelectionEntityListener(
    private val boardSize: Int,
) : EntityListener {
    private val moveValidator = MoveValidator()

    private val capturableFamily = Family.all(CanBeCapturedComponent::class.java).get()
    private val boardSquareFamily =
        Family
            .all(
                PositionComponent::class.java,
                WeatherEventComponent::class.java,
            ).exclude(PieceTypeComponent::class.java)
            .get()

    override fun entityAdded(entity: Entity?) {
        if (AbilityCardComponent.mapper.get(entity) != null) return
        for (piece in EcsEngine.getEntitiesFor(capturableFamily)) {
            piece.remove(CanBeCapturedComponent::class.java)
        }

        if (PlayerColorComponent.mapper.get(entity).color != GameSettings.clientPlayerColor) {
            return
        }

        val availablePositions =
            moveValidator.checkAvailablePositions(
                PlayerColorComponent.mapper.get(entity).color,
                PositionComponent.mapper.get(entity).position,
                MovementRuleComponent.mapper.get(entity),
                boardSize,
            )

        val availablePositionsSet = availablePositions.toSet()

        for (boardSquare in EcsEngine.getEntitiesFor(boardSquareFamily)) {
            if (availablePositionsSet.contains(PositionComponent.mapper.get(boardSquare).position)) {
                HighlightComponent.mapper.get(boardSquare).color = Color(0.5f, 0.5f, 0.5f, 1f)
            }
        }

        entity?.add(ValidMovesComponent(availablePositions))
    }

    override fun entityRemoved(entity: Entity?) {
        if (AbilityCardComponent.mapper.get(entity) != null) return
        entity?.remove(ValidMovesComponent::class.java)

        for (piece in EcsEngine.getEntitiesFor(capturableFamily)) {
            piece.remove(CanBeCapturedComponent::class.java)
        }

        for (boardSquare in EcsEngine.getEntitiesFor(boardSquareFamily)) {
            HighlightComponent.mapper.get(boardSquare).color = Color.WHITE
        }
    }
}
