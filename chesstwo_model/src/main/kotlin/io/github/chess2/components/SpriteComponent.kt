package io.github.chess2.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.assets.toInternalFile

/**
 * @param spriteName - Should just be relative location in assets folder.
 */
class SpriteComponent(spriteName: String, renderPosX: Float, renderPosY: Float) : Component{
    val sprite: Sprite =  Sprite(Texture(spriteName.toInternalFile()))

    init {
        sprite.setPosition(renderPosX, renderPosY)
    }
}
