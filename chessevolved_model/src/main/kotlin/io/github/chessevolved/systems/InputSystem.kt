package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import io.github.chessevolved.components.AbilityCardComponent
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.CanBeCapturedComponent
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.ClickEventComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.WeatherEventComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.singletons.EcsEngine

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
        val selectedEntity = engine.getEntitiesFor(Family.all(SelectionComponent::class.java).get()).firstOrNull()
        val canBeCapturedComponent = piece.getComponent(CanBeCapturedComponent::class.java)

        if (selectionComponent != null) {
            piece.remove(SelectionComponent::class.java)
        } else if (selectedEntity != null && canBeCapturedComponent != null) {
            piece.add(CapturedComponent())
        } else {
            if (selectedEntity != null &&
                AbilityCardComponent.mapper.get(selectedEntity) != null &&
                AbilityCardComponent.mapper.get(selectedEntity).isInInventory
            ) {
                if (AbilityComponent.mapper.get(piece) != null) {
                    println("Already has ability")
                } else {
                    val abilityComponent = AbilityComponent.mapper.get(selectedEntity)

                    if (abilityComponent != null) {
                        piece.add(
                            AbilityComponent(
                                abilityComponent.ability,
                                abilityComponent.abilityCooldownTime,
                                abilityComponent.currentAbilityCDTime,
                            ),
                        )
                    }

                    println("Ability got applied to piece!")
                    selectedEntity.removeAll() // Remove abilityCard-entity from the game.
                }
            } else {
                selectedEntity?.remove(SelectionComponent::class.java)
                piece.add(SelectionComponent())
            }
        }
    }

    private fun handleBoardSquareClicked(boardSquare: Entity) {
        val position = PositionComponent.mapper.get(boardSquare).position

        EcsEngine
            .getEntitiesFor(Family.all(PieceTypeComponent::class.java, SelectionComponent::class.java).get())
            .firstOrNull()
            ?.add(MovementIntentComponent(position))
    }

    private fun handleAbilityCardClicked(abilityCard: Entity) {
        val selectionComponent = SelectionComponent.mapper.get(abilityCard)
        val alreadySelectedEntity = engine.getEntitiesFor(Family.all(SelectionComponent::class.java).get()).firstOrNull()

        if (selectionComponent != null) {
            abilityCard.remove(SelectionComponent::class.java)
        } else {
            alreadySelectedEntity?.remove(SelectionComponent::class.java)
            abilityCard.add(SelectionComponent())
        }
    }
}

class InputService {
    fun clickPieceAtPosition(position: Position) {
        val entity =
            EcsEngine
                .getEntitiesFor(Family.all(PieceTypeComponent::class.java).get())
                .find { PositionComponent.mapper.get(it).position == position }

        entity?.add(ClickEventComponent())
    }

    fun clickBoardSquareAtPosition(position: Position) {
        val entity =
            EcsEngine
                .getEntitiesFor(Family.all(WeatherEventComponent::class.java).get())
                .find { PositionComponent.mapper.get(it).position == position }

        entity?.add(ClickEventComponent())
    }

    fun clickAbilityCardWithId(abilityCardId: Int) {
        val entity =
            EcsEngine
                .getEntitiesFor(Family.all(AbilityCardComponent::class.java).get())
                .find { AbilityCardComponent.mapper.get(it).id == abilityCardId }

        entity?.add(ClickEventComponent())
    }

    fun confirmAbilityChoice() {
        val entityWithSelectionComponent =
            EcsEngine.getEntitiesFor(Family.all(SelectionComponent::class.java).get()).firstOrNull() ?: return
        val abilityCardComponent = AbilityCardComponent.mapper.get(entityWithSelectionComponent) ?: return
        if (abilityCardComponent.isInInventory) return

        abilityCardComponent.isInInventory = true
        entityWithSelectionComponent.remove(SelectionComponent::class.java)

        // Remove all other not-in-inventory cards
        EcsEngine
            .getEntitiesFor(Family.all(AbilityCardComponent::class.java).get())
            .filter {
                !AbilityCardComponent.mapper.get(it).isInInventory
            }.forEach {
                it.removeAll()
            }
    }
}
