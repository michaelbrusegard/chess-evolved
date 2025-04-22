package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.scenes.scene2d.Actor
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.FowComponent
import io.github.chessevolved.components.MovementIntentComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.SelectionComponent
import io.github.chessevolved.components.ValidMovesComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.singletons.EcsEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MovementSystemTest {
    private lateinit var system: MovementSystem
    private lateinit var onTurnComplete: () -> Unit

    /**
     * Resets ECS state and adds a fresh instance of the MovementSystem
     * before each test.
     */
    @BeforeEach
    fun setUp() {
        EcsEngine.removeAllEntities()
        EcsEngine.systems.toList().forEach { EcsEngine.removeSystem(it) }
        onTurnComplete = mock()
        system = MovementSystem(onTurnComplete)
        EcsEngine.addSystem(system)
    }

    /**
     * Test that the system correctly processes a valid move
     * and updates the entity's position.
     */
    @Test
    fun `test that a valid move is processed`() {
        val initial = Position(1, 1)
        val target = Position(2, 2)

        val actor = mock<Actor>()
        val entity =
            Entity().apply {
                add(PositionComponent(initial))
                add(SelectionComponent())
                add(ValidMovesComponent(mutableListOf(target)))
                add(MovementIntentComponent(target))
                add(ActorComponent(actor))
                add(MovementRuleComponent())
            }

        EcsEngine.addEntity(entity)
        EcsEngine.update(0f)

        assertEquals(target, entity.getComponent(PositionComponent::class.java).position)
        verify(actor).setPosition(2f, 2f)

        assertNull(entity.getComponent(SelectionComponent::class.java))
        assertNull(entity.getComponent(ValidMovesComponent::class.java))
        assertNull(entity.getComponent(MovementIntentComponent::class.java))
        verify(onTurnComplete).invoke()
    }

    /**
     * Ensures that a move is ignored if the target is not in the
     * entity's validMoves list.
     */
    @Test
    fun `test that a move to an invalid position is ignored`() {
        val initial = Position(1, 1)
        val target = Position(2, 2) // Not in validMoves

        val entity =
            Entity().apply {
                add(PositionComponent(initial))
                add(SelectionComponent())
                add(ValidMovesComponent(mutableListOf(Position(0, 0))))
                add(MovementIntentComponent(target))
                add(ActorComponent(mock()))
                add(MovementRuleComponent())
            }

        EcsEngine.addEntity(entity)
        EcsEngine.update(0f)

        assertEquals(initial, entity.getComponent(PositionComponent::class.java).position)
        assertNotNull(entity.getComponent(MovementIntentComponent::class.java))
        verify(onTurnComplete, never()).invoke()
    }

    /**
     * Verifies that the fog of war is cleared in a 3x3 grid
     */
    @Test
    fun `test that fog of war is cleared in 3x3 grid around the target`() {
        val target = Position(5, 5)
        val actor = mock<Actor>()
        val entity =
            Entity().apply {
                add(PositionComponent(Position(0, 0)))
                add(SelectionComponent())
                add(ValidMovesComponent(mutableListOf(target)))
                add(MovementIntentComponent(target))
                add(ActorComponent(actor))
                add(MovementRuleComponent())
            }

        val fogTiles = mutableListOf<Entity>()
        for (dx in -1..1) {
            for (dy in -1..1) {
                val pos = Position(target.x + dx, target.y + dy)
                val fogTile =
                    Entity().apply {
                        add(PositionComponent(pos))
                        add(FowComponent(showFog = true))
                    }
                fogTiles.add(fogTile)
                EcsEngine.addEntity(fogTile)
            }
        }

        EcsEngine.addEntity(entity)
        EcsEngine.update(0f)
        fogTiles.forEach {
            val fow = it.getComponent(FowComponent::class.java)
            assertFalse(fow.showFog, "Fog should be cleared at ${it.getComponent(PositionComponent::class.java).position}")
        }
    }
}
