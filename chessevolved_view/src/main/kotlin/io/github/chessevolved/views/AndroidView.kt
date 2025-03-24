package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport

class AndroidView {
    private val camera: OrthographicCamera = OrthographicCamera()
    private var viewport: FitViewport
    private val batch: SpriteBatch = SpriteBatch()

    init {
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.update()
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)
        viewport.setScreenBounds(0, 0, Gdx.graphics.width, Gdx.graphics.height)
    }

    fun beginBatch() {
        viewport.apply()
        batch.projectionMatrix = viewport.camera.combined
        batch.begin()
    }

    fun endBatch() {
        batch.end()
    }

    fun render(sprite: Sprite) {
        sprite.draw(batch)
    }

    fun dispose() {
        batch.dispose()
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
        camera.update()
    }
}
