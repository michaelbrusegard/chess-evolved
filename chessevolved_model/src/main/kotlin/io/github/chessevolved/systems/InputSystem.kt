package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.AbilityCardComponent
import io.github.chessevolved.components.CanBeCapturedComponent
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.ClickEventComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.WeatherEventComponent
import io.github.chessevolved.singletons.ECSEngine

class InputSystem :
    IteratingSystem(
        Family.all(ClickEventComponent::class.java).get(),
    ) {
    override fun processEntity(
        entity: Entity?,
        deltaTime: Float,
    ) {
        if (entity?.getComponent(PieceTypeComponent::class.java) != null) {
            handlePieceClicked(entity)
        } else if (entity?.getComponent(WeatherEventComponent::class.java) != null) {
            handleBoardSquareClicked(entity)
        } else if (entity?.getComponent(AbilityCardComponent::class.java) != null) {
            handleAbilityCardClicked(entity)
        }

        entity?.remove(ClickEventComponent::class.java)
    }

    private fun handlePieceClicked(piece: Entity) {
        val selectionComponent = piece.getComponent(SelectionComponent::class.java)
        val selectedPiece = engine.getEntitiesFor(Family.all(SelectionComponent::class.java).get()).firstOrNull()
        val canBeCapturedComponent = piece.getComponent(CanBeCapturedComponent::class.java)

        if (selectionComponent != null) {
            piece.remove(SelectionComponent::class.java)
        } else if (selectedPiece != null && canBeCapturedComponent != null) {
            piece.add(CapturedComponent())
        } else {
            selectedPiece?.remove(SelectionComponent::class.java)
            piece.add(SelectionComponent())
        }
    }

    private fun handleBoardSquareClicked(boardSquare: Entity) {
        val position = PositionComponent.mapper.get(boardSquare).position

        ECSEngine
            .getEntitiesFor(Family.all(PieceTypeComponent::class.java, SelectionComponent::class.java).get())
            .firstOrNull()
            ?.add(MovementIntentComponent(position))
    }

    private fun handleAbilityCardClicked(abilityCard: Entity) {
        println("Ability card clicked")
        // TODO: Give selected component, de-select all other ability-cards, set highlightComponent to white (selected color)
    }
}

class InputService {
    fun clickPieceAtPosition(position: Position) {
        val entity =
            ECSEngine
                .getEntitiesFor(Family.all(PieceTypeComponent::class.java).get())
                .find { PositionComponent.mapper.get(it).position == position }

        entity?.add(ClickEventComponent())
    }

    fun clickBoardSquareAtPosition(position: Position) {
        val entity =
            ECSEngine
                .getEntitiesFor(Family.all(WeatherEventComponent::class.java).get())
                .find { PositionComponent.mapper.get(it).position == position }

        entity?.add(ClickEventComponent())
    }

    fun clickAbilityCardWithId(abilityCardId: Int) {
        val entity =
            ECSEngine
                .getEntitiesFor(Family.all(AbilityCardComponent::class.java).get())
                .find { AbilityCardComponent.mapper.get(it).id == abilityCardId }

        entity?.add(ClickEventComponent())
    }
}
