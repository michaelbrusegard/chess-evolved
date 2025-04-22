package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.data.MovementPattern
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.MoveType
import io.github.chessevolved.enums.PieceType
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.singletons.EcsEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoveValidatorTest {
    private val boardSize = 8
    private lateinit var validator: MoveValidator

    /**
     * Resets the ECS engine and initializes a new MoveValidator
     * before each test.
     */
    @BeforeEach
    fun setUp() {
        EcsEngine.removeAllEntities()
        validator = MoveValidator()
    }

    /**
     * Injects a MovementPattern into a MovementRuleComponent using reflection.
     */
    private fun injectPattern(
        component: MovementRuleComponent,
        pattern: MovementPattern,
    ) {
        val field = component::class.java.getDeclaredField("patterns")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.get(component) as MutableList<MovementPattern>).add(pattern)
    }

    /**
     * Tests movement in an empty direction.
     */
    @Test
    fun `test movement in an empty direction`() {
        val movementPattern =
            MovementPattern(
                moveName = "rook",
                directions = listOf(Vector2(1f, 0f)),
                maxSteps = 3,
                moveType = MoveType.MOVE_ONLY,
                canJump = false,
            )

        val movementRuleComponent = MovementRuleComponent()
        injectPattern(movementRuleComponent, movementPattern)
        val entity =
            Entity().apply {
                add(PositionComponent(Position(2, 2)))
                add(PieceTypeComponent(PieceType.ROOK))
                add(PlayerColorComponent(PlayerColor.WHITE))
                add(movementRuleComponent)
            }

        EcsEngine.addEntity(entity)
        val result =
            validator.checkAvailablePositions(
                PlayerColor.WHITE,
                Position(2, 2),
                movementRuleComponent,
                boardSize,
            )

        val expected = listOf(Position(3, 2), Position(4, 2), Position(5, 2))
        assertEquals(expected, result)
    }

    /**
     * Tests that a piece cannot move through or onto another piece of the same color.
     */
    @Test
    fun `test that blocked by the same color`() {
        val blocker =
            Entity().apply {
                add(PositionComponent(Position(3, 2)))
                add(PieceTypeComponent(PieceType.PAWN))
                add(PlayerColorComponent(PlayerColor.WHITE))
            }

        val movementPattern =
            MovementPattern(
                moveName = "rook",
                directions = listOf(Vector2(1f, 0f)),
                maxSteps = 3,
                moveType = MoveType.MOVE_ONLY,
                canJump = false,
            )

        val movementRuleComponent = MovementRuleComponent()
        injectPattern(movementRuleComponent, movementPattern)
        val mover =
            Entity().apply {
                add(PositionComponent(Position(2, 2)))
                add(PieceTypeComponent(PieceType.ROOK))
                add(PlayerColorComponent(PlayerColor.WHITE))
                add(movementRuleComponent)
            }

        EcsEngine.addEntity(mover)
        EcsEngine.addEntity(blocker)

        val result =
            validator.checkAvailablePositions(
                PlayerColor.WHITE,
                Position(2, 2),
                movementRuleComponent,
                boardSize,
            )

        assertTrue(result.isEmpty(), "Expected no valid moves due to same-color block")
    }

    /**
     * Tests that an enemy piece can be captured if it's in the move path.
     */
    @Test
    fun `test that enemy piece can be captured`() {
        val enemy =
            Entity().apply {
                add(PositionComponent(Position(3, 2)))
                add(PieceTypeComponent(PieceType.PAWN))
                add(PlayerColorComponent(PlayerColor.BLACK))
            }

        val movementPattern =
            MovementPattern(
                moveName = "rook",
                directions = listOf(Vector2(1f, 0f)),
                maxSteps = 3,
                moveType = MoveType.NORMAL,
                canJump = false,
            )

        val movementRuleComponent = MovementRuleComponent()
        injectPattern(movementRuleComponent, movementPattern)
        val mover =
            Entity().apply {
                add(PositionComponent(Position(2, 2)))
                add(PieceTypeComponent(PieceType.ROOK))
                add(PlayerColorComponent(PlayerColor.WHITE))
                add(movementRuleComponent)
            }

        EcsEngine.addEntity(mover)
        EcsEngine.addEntity(enemy)

        val result =
            validator.checkAvailablePositions(
                PlayerColor.WHITE,
                Position(2, 2),
                movementRuleComponent,
                boardSize,
            )

        assertEquals(listOf(Position(3, 2)), result)
    }

    /**
     * Tests that a piece cannot capture an enemy if there is a piece in between
     * and the movement pattern does not allow for jumping.
     */
    @Test
    fun `test capture blocked by that the canJump is false`() {
        val blocker =
            Entity().apply {
                add(PositionComponent(Position(3, 2)))
                add(PieceTypeComponent(PieceType.PAWN))
                add(PlayerColorComponent(PlayerColor.WHITE))
            }

        val enemy =
            Entity().apply {
                add(PositionComponent(Position(4, 2)))
                add(PieceTypeComponent(PieceType.PAWN))
                add(PlayerColorComponent(PlayerColor.BLACK))
            }

        val movementPattern =
            MovementPattern(
                moveName = "rook",
                directions = listOf(Vector2(1f, 0f)),
                maxSteps = 5,
                moveType = MoveType.NORMAL,
                canJump = false,
            )

        val movementRuleComponent = MovementRuleComponent()
        injectPattern(movementRuleComponent, movementPattern)
        val mover =
            Entity().apply {
                add(PositionComponent(Position(2, 2)))
                add(PieceTypeComponent(PieceType.ROOK))
                add(PlayerColorComponent(PlayerColor.WHITE))
                add(movementRuleComponent)
            }

        EcsEngine.addEntity(mover)
        EcsEngine.addEntity(blocker)
        EcsEngine.addEntity(enemy)
        val result =
            validator.checkAvailablePositions(
                PlayerColor.WHITE,
                Position(2, 2),
                movementRuleComponent,
                boardSize,
            )

        assertTrue(result.isEmpty())
    }

    /**
     * Tests that a captureOnly pattern does not allow movement to empty tiles.
     */
    @Test
    fun `test capture only ignores the empty tiles`() {
        val movementPattern =
            MovementPattern(
                moveName = "captureOnly",
                directions = listOf(Vector2(1f, 1f)),
                maxSteps = 3,
                moveType = MoveType.CAPTURE_ONLY,
                canJump = false,
            )

        val movementRuleComponent = MovementRuleComponent()
        injectPattern(movementRuleComponent, movementPattern)
        val mover =
            Entity().apply {
                add(PositionComponent(Position(2, 2)))
                add(PieceTypeComponent(PieceType.BISHOP))
                add(PlayerColorComponent(PlayerColor.WHITE))
                add(movementRuleComponent)
            }

        EcsEngine.addEntity(mover)
        val result =
            validator.checkAvailablePositions(
                PlayerColor.WHITE,
                Position(2, 2),
                movementRuleComponent,
                boardSize,
            )

        assertTrue(result.isEmpty(), "CAPTURE_ONLY should not return empty tiles")
    }
}
