package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.singletons.EcsEngine
import org.junit.jupiter.api.Test
import kotlin.test.assertFails

class PieceFactoryTest {
    private val engine: Engine = EcsEngine
    private val assetManager: AssetManager = AssetManager()
    private val pieceFactory: PieceFactory = PieceFactory(engine, assetManager)

    /**
     * Attempt to create a pawn at an invalid location
     */
    @Test
    fun createPawn() {
        assertFails({
            pieceFactory.createPawn(
                false,
                Position(1000, 1000),
                PlayerColor.BLACK,
                Stage(),
            )
        })
    }

    /**
     * Attempt to create a knight at an invalid location
     */
    @Test
    fun createKnight() {
        assertFails({
            pieceFactory.createKnight(
                Position(1000, 1000),
                PlayerColor.BLACK,
                Stage(),
            )
        })
    }

    /**
     * Attempt to create a bishop at an invalid location
     */
    @Test
    fun createBishop() {
        assertFails({
            pieceFactory.createBishop(
                Position(1000, 1000),
                PlayerColor.BLACK,
                Stage(),
            )
        })
    }

    /**
     * Attempt to create a rook at an invalid location
     */
    @Test
    fun createRook() {
        assertFails({
            pieceFactory.createRook(
                Position(1000, 1000),
                PlayerColor.BLACK,
                Stage(),
            )
        })
    }

    /**
     * Attempt to create a queen at an invalid location
     */
    @Test
    fun createQueen() {
        assertFails({
            pieceFactory.createQueen(
                Position(1000, 1000),
                PlayerColor.BLACK,
                Stage(),
            )
        })
    }

    /**
     * Attempt to create a king at an invalid location
     */
    @Test
    fun createKing() {
        assertFails({
            pieceFactory.createKing(
                Position(1000, 1000),
                PlayerColor.BLACK,
                Stage(),
            )
        })
    }
}
