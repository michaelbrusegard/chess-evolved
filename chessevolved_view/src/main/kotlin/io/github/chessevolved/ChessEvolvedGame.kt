package io.github.chessevolved

import io.github.chessevolved.views.DesktopView
import io.github.chessevolved.views.IView
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync

class ChessEvolvedGame : KtxGame<KtxScreen>(){
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    var currentView: IView = DesktopView()

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        currentView.render()
    }
}
