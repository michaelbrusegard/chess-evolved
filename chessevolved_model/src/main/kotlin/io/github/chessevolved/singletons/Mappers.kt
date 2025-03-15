package io.github.chessevolved.singletons

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import io.github.chessevolved.components.BoardSizeComponent
import io.github.chessevolved.components.ChessBoardSpriteComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SpriteComponent

object Mappers {
    private val boardSize: ComponentMapper<BoardSizeComponent> = ComponentMapper.getFor(BoardSizeComponent::class.java)
    private val position: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)
    private val chessboardSprite: ComponentMapper<ChessBoardSpriteComponent> = ComponentMapper.getFor(ChessBoardSpriteComponent::class.java)
    private val sprite: ComponentMapper<SpriteComponent> = ComponentMapper.getFor(SpriteComponent::class.java)

    fun getBoardSize(entity: Entity): BoardSizeComponent {
        return boardSize.get(entity)
    }

    fun getPosition(entity: Entity): PositionComponent {
        return position.get(entity)
    }

    fun getChessboardSprite(entity: Entity): ChessBoardSpriteComponent {
        return chessboardSprite.get(entity)
    }

    fun getSprite(entity: Entity): SpriteComponent {
        return sprite.get(entity)
    }
}
