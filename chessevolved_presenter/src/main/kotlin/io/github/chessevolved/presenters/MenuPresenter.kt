package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.views.MenuView

class MenuPresenter(
    private val menuView: MenuView,
    private val navigator: Navigator,
) : IPresenter {
    init {
        menuView.onCreateLobbyButtonClicked = { enterCreateGame() }
        menuView.onJoinGameButtonClicked = { enterJoinGame() }
        menuView.init()
    }

    override fun update(dt: Float) {
    }

    override fun render(sb: SpriteBatch) {
        menuView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        menuView.resize(width, height)
    }

    override fun dispose() {
        menuView.dispose()
    }

    override fun setInputProcessor() {
        menuView.setInputProcessor()
    }

    fun enterJoinGame() {
        navigator.navigateToJoinGame()
    }

    fun enterCreateGame() {
        // TODO: Implement actual lobby creation logic here or delegate
        val newLobbyId = "ABCDEF"
        navigator.navigateToCreateLobby(newLobbyId)
    }
}
