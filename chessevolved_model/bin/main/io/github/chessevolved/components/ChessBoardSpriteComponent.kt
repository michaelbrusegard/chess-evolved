package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.assets.toInternalFile

class ChessBoardSpriteComponent: Component {
    val blackTileSprite: Sprite = Sprite(Texture("board/BlackTile.png".toInternalFile()))
    val whiteTileSprite: Sprite = Sprite(Texture("board/WhiteTile.png".toInternalFile()))
}
