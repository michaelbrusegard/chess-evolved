package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import ktx.actors.onClick

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
        val filename = "pieces/$colorStr-$typeStr.png"

        val texture = assetManager.get(filename, Texture::class.java)
        return TextureRegion(texture)
    }

    private fun getPieceActor(
        position: Position,
        stage: Stage,
        onClick: (Position) -> Unit
    ): Image {
        val image = Image().apply {
            setSize(1f, 1f)
            setPosition(position.x.toFloat(), position.y.toFloat())

            onClick {
                onClick(position)
            }
        }
        stage.addActor(image)

        return image
    }

    private fun createPiece(
        position: Position,
        pieceType: PieceType,
        playerColor: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(PieceTypeComponent(pieceType))
            add(PlayerColorComponent(playerColor))
            add(AbilityComponent(emptyList()))
            add(TextureRegionComponent(getPieceTextureRegion(pieceType, playerColor)))
            add(ActorComponent(getPieceActor(position, stage, onClick)))
            engine.addEntity(this)
        }

    fun createPawn(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) = createPiece(position, PieceType.PAWN, color, stage, onClick)

    fun createKnight(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) = createPiece(position, PieceType.KNIGHT, color, stage, onClick)

    fun createBishop(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) = createPiece(position, PieceType.BISHOP, color, stage, onClick)

    fun createRook(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) = createPiece(position, PieceType.ROOK, color, stage, onClick)

    fun createQueen(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) = createPiece(position, PieceType.QUEEN, color, stage, onClick)

    fun createKing(
        position: Position,
        color: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit
    ) = createPiece(position, PieceType.KING, color, stage, onClick)
}
