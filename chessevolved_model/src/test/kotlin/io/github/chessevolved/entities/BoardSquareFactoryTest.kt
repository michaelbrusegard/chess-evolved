package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.enums.WeatherEvent
import io.github.chessevolved.singletons.EcsEngine
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class BoardSquareFactoryTest {
    private val engine: Engine = EcsEngine
    private val assetManager: AssetManager = AssetManager()
    private val boardSquareFactory: BoardSquareFactory = BoardSquareFactory(engine, assetManager)

    /**
     * Attempt to create a square at an invalid location
     */
    @Test
    fun createBoardSquare() {
        assertFails({
            boardSquareFactory.createBoardSquare(
                Position(1000, 1000),
                WeatherEvent.NONE,
                PlayerColor.BLACK,
                Stage(),
            )
        })
    }
}
