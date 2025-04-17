package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.singletons.ECSEngine
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class BoardSquareFactoryTest {
    private val engine: Engine = ECSEngine
    private val assetManager: AssetManager = AssetManager()
    private val boardSquareFactory: BoardSquareFactory = BoardSquareFactory(engine, assetManager)

    @Test
    fun createBoardSquare() {
        assetManager.load("board/black-tile.png", Texture::class.java)
        assetManager.finishLoading()

        boardSquareFactory.createBoardSquare(
            Position(1,1),
            WeatherEvent.NONE,
            PlayerColor.BLACK
        )
        val texture: TextureRegion = TextureRegion(Texture("board/black-tile.png"))
        assertEquals(texture, boardSquareFactory.getBoardSquareTextureRegion(PlayerColor.BLACK))
    }

}
