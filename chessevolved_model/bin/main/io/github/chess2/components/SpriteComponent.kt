package io.github.chess2.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite

/**
 * @param spriteName - Should just be relative location in assets folder.
 */
class SpriteComponent(spriteName: String) : Component{
    var batch: Sprite =  Sprite(Texture(Gdx.files.internal(spriteName)))
}
