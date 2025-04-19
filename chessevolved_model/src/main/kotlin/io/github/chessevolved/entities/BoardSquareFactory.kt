package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.components.WeatherEventComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.enums.WeatherEvent
import io.github.chessevolved.systems.InputService
import ktx.actors.onClick

class BoardSquareFactory(
    private val engine: Engine,
    private val assetManager: AssetManager,
) {
    private val inputService: InputService = InputService()

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
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(WeatherEventComponent(weatherEvent))
            add(PlayerColorComponent(playerColor))
            add(TextureRegionComponent(getBoardSquareTextureRegion(playerColor)))
            add(HighlightComponent(Color.WHITE))
            add(
                ActorComponent(
                    getBoardActor(position, stage) { clickedPosition -> inputService.clickBoardSquareAtPosition(clickedPosition) },
                ),
            )
            engine.addEntity(this)
        }
}
