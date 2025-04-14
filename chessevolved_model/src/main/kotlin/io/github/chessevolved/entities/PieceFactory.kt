package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.singletons.ComponentMappers
import ktx.actors.onClick
import ktx.ashley.get
import ktx.math.vec2

class PieceFactory(
    private val engine: Engine,
    private val assetManager: AssetManager,
) {
    private fun getPieceTextureRegion(
        pieceType: PieceType,
        playerColor: PlayerColor,
    ): TextureRegion {
        val colorStr = playerColor.name.lowercase()
        val typeStr = pieceType.name.lowercase()
        val filename = "pieces/$typeStr-$colorStr.png"

        val texture = assetManager.get(filename, Texture::class.java)
        return TextureRegion(texture)
    }

    private fun getPieceActor(
        positionProvider: () -> Position,
        stage: Stage,
        onClick: (Position) -> Unit
    ): Image {
        val image = Image().apply {
            setSize(1f, 1f)
            setPosition(positionProvider().x.toFloat(), positionProvider().y.toFloat())

            onClick {
                onClick(positionProvider())
            }
        }
        stage.addActor(image)

        return image
    }

    private fun createPiece(
        position: Position,
        pieceType: PieceType,
        playerColor: PlayerColor,
        movementRuleComponent: MovementRuleComponent,
        stage: Stage,
        onClick: (Position) -> Unit
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(PieceTypeComponent(pieceType))
            add(PlayerColorComponent(playerColor))
            add(movementRuleComponent)
            add(AbilityComponent(emptyList()))
            add(TextureRegionComponent(getPieceTextureRegion(pieceType, playerColor)))
            add(ActorComponent(getPieceActor(
                positionProvider = { ComponentMappers.posMap.get(this).position},
                stage,
                onClick)))
            engine.addEntity(this)
        }

    fun createPawn(
        isPlayerOne: Boolean,
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) : Entity
    {
        val movementRuleComponent = MovementRuleComponent()
        val normalDirections: ArrayList<Vector2> = arrayListOf(
            vec2(0f, if(isPlayerOne) 1f else -1f)
        )
        // Todo add killing directions

        movementRuleComponent.addMovementRule(
            "pawnNormal",
            normalDirections,
            1,
            false,
            true,
            false
        )

        return createPiece(position, PieceType.PAWN, color, movementRuleComponent, stage, onClick)
    }

    fun createKnight(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) : Entity
    {
        val movementRuleComponent = MovementRuleComponent()
        val normalDirections: ArrayList<Vector2> = arrayListOf(
            vec2(1f, 2f), vec2(-1f, 2f),
            vec2(1f, -2f), vec2(-1f, -2f),
            vec2(2f, 1f), vec2(2f, -1f),
            vec2(-2f, 1f), vec2(-2f, -1f),
        )

        movementRuleComponent.addMovementRule(
            "knightNormal",
            normalDirections,
            1,
            true,
            true,
            true
        )

        return createPiece(position, PieceType.KNIGHT, color, movementRuleComponent, stage, onClick)
    }

    fun createBishop(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) : Entity
    {
        val movementRuleComponent = MovementRuleComponent()
        val normalDirections: ArrayList<Vector2> = arrayListOf(
            vec2(1f, 1f), vec2(-1f, 1f),
            vec2(1f, -1f), vec2(-1f, -1f)
        )

        movementRuleComponent.addMovementRule(
            "bishopNormal",
            normalDirections,
            0,
            false,
            true,
            true
        )

        return createPiece(position, PieceType.BISHOP, color, movementRuleComponent, stage, onClick)
    }

    fun createRook(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) : Entity
    {
        val movementRuleComponent = MovementRuleComponent()
        val normalDirections: ArrayList<Vector2> = arrayListOf(
            vec2(0f, 1f), vec2(0f, -1f),
            vec2(1f, 0f), vec2(-1f, 0f)
        )

        movementRuleComponent.addMovementRule(
            "rookNormal",
            normalDirections,
            0,
            false,
            true,
            true
        )

        return createPiece(position, PieceType.ROOK, color, movementRuleComponent, stage, onClick)
    }

    fun createQueen(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) : Entity
    {
        val movementRuleComponent = MovementRuleComponent()
        val normalDirections: ArrayList<Vector2> = arrayListOf(
            // Rook type movement
            vec2(0f, 1f), vec2(0f, -1f),
            vec2(1f, 0f), vec2(-1f, 0f),
            // Bishop type movement
            vec2(1f, 1f), vec2(-1f, 1f),
            vec2(1f, -1f), vec2(-1f, -1f)
        )

        movementRuleComponent.addMovementRule(
            "queenNormal",
            normalDirections,
            0,
            false,
            true,
            true
        )

        return createPiece(position, PieceType.QUEEN, color, movementRuleComponent, stage, onClick)
    }

    fun createKing(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) : Entity
    {
        val movementRuleComponent = MovementRuleComponent()
        val normalDirections: ArrayList<Vector2> = arrayListOf(
            // Rook type movement
            vec2(0f, 1f), vec2(0f, -1f),
            vec2(1f, 0f), vec2(-1f, 0f),
            // Bishop type movement
            vec2(1f, 1f), vec2(-1f, 1f),
            vec2(1f, -1f), vec2(-1f, -1f)
        )

        movementRuleComponent.addMovementRule(
            "queenNormal",
            normalDirections,
            1,
            false,
            true,
            true
        )

        return createPiece(position, PieceType.KING, color, movementRuleComponent, stage, onClick)
    }
}
