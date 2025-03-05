package io.github.chess2

import io.github.chess2.presenters.GamePresenter
import io.github.chess2.presenters.IPresenter
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync

class Chess2Game : KtxGame<KtxScreen>(){
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val gamePresenter: IPresenter = GamePresenter()

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        gamePresenter.render()
    }
}
