package io.github.chessevolved.singletons

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.WeatherEventComponent

object EntityFamilies {
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

    /**
     * Getter for the family of piece entities.
     * @return ImmutableArray of piece entities.
     */
    fun getPieceEntities(): ImmutableArray<Entity> {
        return ECSEngine.getEntitiesFor(pieceFamily)
    }

    /**
     * Getter for the family of boardSquare entities.
     * @return ImmutableArray of boardSquare entities.
     */
    fun getBoardSquareEntities(): ImmutableArray<Entity> {
        return ECSEngine.getEntitiesFor(boardSquareFamily)
    }
}
