package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.CanBeCapturedComponent
import io.github.chessevolved.components.CapturedComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.PieceType
import io.github.chessevolved.singletons.EcsEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CaptureSystemTest {
    private lateinit var system: CaptureSystem

    /**
     * Resets ECS state and adds a fresh instance of the CaptureSystem
     * before each test.
     */
    @BeforeEach
    fun setUp() {
        EcsEngine.removeAllEntities()
        EcsEngine.systems.toList().forEach { EcsEngine.removeSystem(it) }
        system = CaptureSystem()
        EcsEngine.addSystem(system)
    }

    /**
     * Test that the system correctly removes the captured entity
     * and updates the capturing entity's components.
     */
    @Test
    fun `test that entity captured not by ability triggers the movement intent`() {
        val captured =
            Entity().apply {
                add(PositionComponent(Position(2, 3)))
                add(CapturedComponent(capturedByAbility = false))
                add(ActorComponent(mock()))
            }

        val capturing =
            Entity().apply {
                add(PieceTypeComponent(PieceType.KNIGHT))
                add(SelectionComponent())
            }

        EcsEngine.addEntity(captured)
        EcsEngine.addEntity(capturing)
        EcsEngine.update(0f)

        assertNull(EcsEngine.entities.find { it == captured })
        assertNotNull(capturing.getComponent(MovementIntentComponent::class.java))
    }

    /**
     * Verifies that all entities with CanBeCapturedComponent have it removed
     * once the CaptureSystem processes a captured piece.
     */
    @Test
    fun `test that CanBeCapturedComponent is removed`() {
        val piece1 = Entity().apply { add(CanBeCapturedComponent()) }
        val piece2 = Entity().apply { add(CanBeCapturedComponent()) }
        val captured =
            Entity().apply {
                add(PositionComponent(Position(0, 0)))
                add(CapturedComponent())
                add(ActorComponent(mock()))
            }

        EcsEngine.addEntity(piece1)
        EcsEngine.addEntity(piece2)
        EcsEngine.addEntity(captured)
        EcsEngine.update(0f)

        assertNull(piece1.getComponent(CanBeCapturedComponent::class.java))
        assertNull(piece2.getComponent(CanBeCapturedComponent::class.java))
    }
}
