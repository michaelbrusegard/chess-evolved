package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.singletons.ECSEngine
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class PieceFactoryTest {
    private val engine: Engine = ECSEngine
    private val assetManager: AssetManager = AssetManager()
    private val pieceFactory: PieceFactory = PieceFactory(engine, assetManager)

    @Test
    fun createPawn() {
    }

    @Test
    fun createKnight() {
    }

    @Test
    fun createBishop() {
    }

    @Test
    fun createRook() {
        assetManager.load("pieces/black-rook.png", Texture::class.java)
        assetManager.finishLoading()

        pieceFactory.createRook(
            Position(1,1),
            PlayerColor.BLACK
        )
        val texture: TextureRegion = TextureRegion(Texture("pieces/black-rook.png"))
        assertEquals(texture, pieceFactory.getPieceTextureRegion(PieceType.ROOK, PlayerColor.BLACK))
    }

    @Test
    fun createQueen() {
    }

    @Test
    fun createKing() {
    }
}
