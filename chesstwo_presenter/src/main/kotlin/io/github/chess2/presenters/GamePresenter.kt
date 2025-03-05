package io.github.chess2.presenters

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chess2.components.SpriteComponent
import io.github.chess2.singletons.ECSEngine
import io.github.chess2.systems.GameBoardSetup
import io.github.chess2.systems.RenderingSystem

class GamePresenter {
    val engine: ECSEngine = ECSEngine

    val gameBoardSetup: GameBoardSetup = GameBoardSetup()
    val renderer: RenderingSystem = RenderingSystem()

    fun render(): Unit {
        val pieceSpriteList: List<SpriteComponent> = gameBoardSetup.pieces.map { it.getComponent(SpriteComponent::class.java) }

        renderer.renderChessBoard(gameBoardSetup.board)
        renderer.render(pieceSpriteList)
    }
}
