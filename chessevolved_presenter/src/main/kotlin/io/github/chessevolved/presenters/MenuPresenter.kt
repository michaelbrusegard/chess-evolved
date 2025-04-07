package io.github.chessevolved.presenters

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.views.MenuView

class MenuPresenter(
    private val menuView: MenuView,
    private val navigator: Navigator,
) : IPresenter {
    init {
        menuView.onCreateLobbyButtonClicked = { createLobby() }
        menuView.onJoinGameButtonClicked = { navigator.navigateToJoinGame() }
        menuView.init()
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

    private fun createLobby() {
        // TODO: Implement actual lobby creation logic here or delegate
        val newLobbyId = "ABCDEF"
        navigator.navigateToCreateLobby(newLobbyId)
    }
}
