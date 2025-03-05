package io.github.chess2.presenters

import io.github.chess2.components.SpriteComponent
import io.github.chess2.singletons.ECSEngine
import io.github.chess2.systems.GameBoardSetup
import io.github.chess2.systems.RenderingSystem

class GamePresenter : IPresenter {
    val engine: ECSEngine = ECSEngine

    val gameBoardSetup: GameBoardSetup = GameBoardSetup()
    val renderer: RenderingSystem = RenderingSystem()

    override fun render(): Unit {
        val pieceSpriteList: List<SpriteComponent> = gameBoardSetup.pieces.map { it.getComponent(SpriteComponent::class.java) }

        // Preferably rendersystem should be in the view.
        // The specific views handle their own render, and the presenter don't call a render function at all
        renderer.renderChessBoard(gameBoardSetup.board)
        renderer.render(pieceSpriteList)
    }
}
