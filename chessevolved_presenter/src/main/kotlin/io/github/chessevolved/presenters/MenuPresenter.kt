package io.github.chessevolved.presenters
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.Navigator
import io.github.chessevolved.singletons.Lobby
import io.github.chessevolved.singletons.Lobby.getLobbyId
import io.github.chessevolved.views.MenuView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        runBlocking {
            launch {
                try {
                    Lobby.createLobby()
                    navigator.navigateToCreateLobby(getLobbyId() ?: throw Exception("Unexpected state when creating lobby!"))
                } catch (e: Exception) {
                    menuView.showCreateGameError(e.message ?: "Internal error!")
                }
            }
        }
    }
}
