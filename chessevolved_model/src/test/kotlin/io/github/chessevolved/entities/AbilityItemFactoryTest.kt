package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.enums.AbilityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

class AbilityItemFactoryTest {
    private lateinit var engine: Engine
    private lateinit var assetManager: AssetManager
    private lateinit var factory: AbilityItemFactory
    private lateinit var mockTexture: TextureRegion

    /**
     * Mock the AssetManager to return a mock Texture for each ability type.
     * This allows us to test the AbilityItemFactory without loading actual textures.
     */
    @BeforeEach
    fun setUp() {
        engine = Engine()
        assetManager = mock(AssetManager::class.java)
        mockTexture = mock(TextureRegion::class.java)
        AbilityType.values().forEach { type ->
            val path = "abilities/cards/${type.toString().lowercase()}Card.png"
            `when`(assetManager.get(path, Texture::class.java)).thenReturn(mock(Texture::class.java))
        }

        factory = AbilityItemFactory(engine, assetManager)
    }

    /**
     * Test that that each ability type created by the factory has the correct cooldown time
     */
    @Test
    fun `test cooldown values`() {
        val expectedCooldowns =
            mapOf(
                AbilityType.SHIELD to 3,
                AbilityType.EXPLOSION to 2,
                AbilityType.SWAP to 2,
                AbilityType.MIRROR to 3,
                AbilityType.NEW_MOVEMENT to 0,
            )

        expectedCooldowns.forEach { (abilityType, expectedCooldown) ->
            val entity = factory.createAbilityItem(abilityType)
            val abilityComponent = entity.getComponent(AbilityComponent::class.java)

            assertEquals(expectedCooldown, abilityComponent.abilityCooldownTime)
        }
    }

    /**
     * Test that the AbilityItemFactory adds one entity to the engine for each ability type.
     */
    @Test
    fun `test entity added to engine`() {
        val initialEntityCount = engine.entities.size()
        AbilityType.values().forEach { abilityType ->
            factory.createAbilityItem(abilityType)
        }

        assertEquals(
            initialEntityCount + AbilityType.values().size,
            engine.entities.size(),
        )
    }
}
