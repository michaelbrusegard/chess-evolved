package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent

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

    private fun createPiece(
        position: Position,
        pieceType: PieceType,
        playerColor: PlayerColor,
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(PieceTypeComponent(pieceType))
            add(PlayerColorComponent(playerColor))
            add(AbilityComponent(""))
            add(TextureRegionComponent(getPieceTextureRegion(pieceType, playerColor)))
            engine.addEntity(this)
        }

    fun createPawn(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.PAWN, color)

    fun createKnight(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.KNIGHT, color)

    fun createBishop(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.BISHOP, color)

    fun createRook(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.ROOK, color)

    fun createQueen(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.QUEEN, color)

    fun createKing(
        position: Position,
        color: PlayerColor,
    ) = createPiece(position, PieceType.KING, color)
}
