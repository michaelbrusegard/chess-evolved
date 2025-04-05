package io.github.chessevolved.presenters
import LobbyPresenter
import io.github.chessevolved.PresenterManager
import io.github.chessevolved.singletons.Lobby.createLobby
import io.github.chessevolved.singletons.Lobby.getLobbyId
import io.github.chessevolved.views.JoinGameView
import io.github.chessevolved.views.LobbyView
import io.github.chessevolved.views.MenuView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MenuPresenter(
    private val view: MenuView,
) : IPresenter {
    override fun render() {
        view.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        view.resize(width, height)
    }

    override fun dispose() {
        view.dispose()
    }

    override fun setInputProcessor() {
        view.setInputProcessor()
    }

    fun enterJoinGame() {
        val joinGamePresenter = JoinGamePresenter(JoinGameView())
        PresenterManager.push(StatePresenter(joinGamePresenter))
    }

    fun enterCreateGame() {
        runBlocking {
            launch {
                try {
                    createLobby()
                    val lobbyPresenter = LobbyPresenter(LobbyView(getLobbyId() ?: throw Exception("Unexpected state when creating lobby!")))
                    PresenterManager.push(StatePresenter(lobbyPresenter))
                } catch (e: Exception) {
                    view.showCreateGameError(e.message ?: "Internal error!")
                }
            }
        }
    }
}
