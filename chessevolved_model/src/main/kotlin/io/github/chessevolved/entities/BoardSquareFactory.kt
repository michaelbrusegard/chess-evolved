package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.components.WeatherEventComponent

class BoardSquareFactory(
    private val engine: Engine,
    private val assetManager: AssetManager,
) {
    private fun getBoardSquareTextureRegion(playerColor: PlayerColor): TextureRegion {
        val colorStr = playerColor.name.lowercase()
        val filename = "board/$colorStr-tile.png"

        val texture = assetManager.get(filename, Texture::class.java)
        return TextureRegion(texture)
    }

    fun createBoardSquare(
        position: Position,
        weatherEvent: WeatherEvent,
        playerColor: PlayerColor,
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(WeatherEventComponent(weatherEvent))
            add(PlayerColorComponent(playerColor))
            add(TextureRegionComponent(getBoardSquareTextureRegion(playerColor)))
            engine.addEntity(this)
        }
}
