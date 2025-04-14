package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.singletons.ComponentMappers
import io.github.chessevolved.singletons.EntityFamilies

class RenderingSystem(
    private val batch: SpriteBatch,
) : IteratingSystem(
        Family.all(PositionComponent::class.java, TextureRegionComponent::class.java).get(),
    ) {

    override fun processEntity(
        entity: Entity,
        deltaTime: Float,
    ) {
        val position = ComponentMappers.posMap.get(entity)
        val texture = ComponentMappers.textureMap.get(entity)

        if (position != null && texture != null) {
            texture.region?.let { region ->
                batch.color = if (texture.isSelected) Color(0.5f, 0.5f, 0.5f, 1f) else Color(1f, 1f, 1f, 1f)
                batch.draw(
                    region,
                    position.position.x.toFloat(),
                    position.position.y.toFloat(),
                    1f,
                    1f,
                )
                // Reset HUE
                batch.color = Color.WHITE
            }
        }
    }

    fun defaultBoardSquaresState() {
        // Should make alle the board squares normal color // i.e not highlighted
        val boardSquares = EntityFamilies.getBoardSquareEntities();

        for (entity in boardSquares) {
            val texture = ComponentMappers.textureMap.get(entity)
            texture.isSelected = false
        }
    }

    fun changeBoardsForPositions(
        boardPositions: MutableList<Position>
    ) {
        val positions = boardPositions.toSet()
        val validBoardSquares = EntityFamilies.getBoardSquareEntities().filter { entity ->
            val pos = ComponentMappers.posMap.get(entity).position
            positions.contains(pos)
        };

        validBoardSquares.map { boardSquare ->
            ComponentMappers.textureMap.get(boardSquare).isSelected = true
        }
    }
}
