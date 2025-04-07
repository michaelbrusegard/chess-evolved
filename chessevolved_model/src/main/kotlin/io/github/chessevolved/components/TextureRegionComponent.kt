package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool.Poolable

class TextureRegionComponent(
    region: TextureRegion? = null,
) : Component,
    Poolable {
    var region = region

    override fun reset() {
        region = null
    }
}
