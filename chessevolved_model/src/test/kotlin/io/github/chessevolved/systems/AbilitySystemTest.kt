package io.github.chessevolved.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityTriggerComponent
import io.github.chessevolved.components.BlockedComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.VisualEffectComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.AbilityType
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.singletons.EcsEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AbilitySystemTest {
    private lateinit var abilitySystem: AbilitySystem
    private lateinit var assetManager: AssetManager

    /**
     * Setup a clean ECS engine state before each test.
     */
    @BeforeEach
    fun setUp() {
        assetManager = mock(AssetManager::class.java)
        EcsEngine.removeAllEntities()
        val systems = EcsEngine.systems.toList()
        systems.forEach { EcsEngine.removeSystem(it) }
        abilitySystem = AbilitySystem()
        EcsEngine.addSystem(abilitySystem)
    }

    /**
     * Verifies that ability cooldown time decrements after
     * the system processes an entity with an ability.
     */
    @Test
    fun `test that cooldown decrements time`() {
        val entity = Entity()
        val abilityComponent = AbilityComponent(AbilityType.EXPLOSION, 5, 3)

        entity.add(abilityComponent)
        entity.add(AbilityTriggerComponent(Position(0, 0), Position(0, 0)))
        entity.add(PlayerColorComponent(PlayerColor.WHITE))
        EcsEngine.addEntity(entity)
        EcsEngine.update(0f)

        assertEquals(2, abilityComponent.currentAbilityCDTime)
    }

    /**
     * Verifies that triggering an EXPLOSION ability
     * creates a new entity with a VisualEffectComponent.
     */
    @Test
    fun `test that explosion effect creates a visual effect`() {
        val entity =
            Entity().apply {
                add(AbilityComponent(AbilityType.EXPLOSION, 5, 0))
                add(AbilityTriggerComponent(Position(0, 0), Position(0, 0)))
                add(PlayerColorComponent(PlayerColor.WHITE))
            }

        EcsEngine.addEntity(entity)
        EcsEngine.update(0f)
        val effectEntities =
            EcsEngine.entities.filter {
                it.getComponent(VisualEffectComponent::class.java) != null
            }

        assertEquals(1, effectEntities.size)
    }

    /**
     * Verifies that triggering a SHIELD ability
     * adds a BlockedComponent to the same entity.
     */
    @Test
    fun `test that shield effect adds a blocked component`() {
        val entity =
            Entity().apply {
                add(AbilityComponent(AbilityType.SHIELD, 5, 0))
                add(AbilityTriggerComponent(Position(1, 1), Position(0, 0)))
            }

        EcsEngine.addEntity(entity)
        EcsEngine.update(0f)
        val blockedComponent = entity.getComponent(BlockedComponent::class.java)

        assert(blockedComponent != null)
    }

    /**
     * Ensures that after an ability is triggered and processed,
     * the AbilityTriggerComponent is removed from the entity.
     */
    @Test
    fun `test that ability trigger component is removed after processing`() {
        val entity =
            Entity().apply {
                add(AbilityComponent(AbilityType.EXPLOSION, 5, 0))
                add(AbilityTriggerComponent(Position(0, 0), Position(0, 0)))
                add(PlayerColorComponent(PlayerColor.WHITE))
            }

        EcsEngine.addEntity(entity)
        EcsEngine.update(0f)

        assertNull(entity.getComponent(AbilityTriggerComponent::class.java))
    }
}
