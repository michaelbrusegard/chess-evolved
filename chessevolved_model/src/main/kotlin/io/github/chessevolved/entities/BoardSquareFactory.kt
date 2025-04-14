package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.components.WeatherEvent
import io.github.chessevolved.components.WeatherEventComponent
import ktx.actors.onClick

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

    private fun getBoardActor(
        position: Position,
        stage: Stage,
        onClick: (Position) -> Unit,
    ): Image {
        val image =
            Image().apply {
                setSize(1f, 1f)
                setPosition(position.x.toFloat(), position.y.toFloat())

                onClick {
                    onClick(position)
                }
            }
        stage.addActor(image)

        return image
    }

    fun createBoardSquare(
        position: Position,
        weatherEvent: WeatherEvent,
        playerColor: PlayerColor,
        stage: Stage,
        onClick: (Position) -> Unit,
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(WeatherEventComponent(weatherEvent))
            add(PlayerColorComponent(playerColor))
            add(TextureRegionComponent(getBoardSquareTextureRegion(playerColor)))
            add(ActorComponent(getBoardActor(position, stage, onClick)))
            engine.addEntity(this)
        }
}
