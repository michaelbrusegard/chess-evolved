import io.github.chessevolved.PresenterManager
import io.github.chessevolved.presenters.IPresenter
import io.github.chessevolved.presenters.SettingsPresenter
import io.github.chessevolved.presenters.StatePresenter
import io.github.chessevolved.singletons.Lobby.leaveLobby
import io.github.chessevolved.views.LobbyView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LobbyPresenter(
    private val lobbyView: LobbyView,
) : IPresenter {
    private val settingsPresenter = SettingsPresenter(SettingsView())

    init {
        lobbyView.onLeaveButtonClicked = { returnToMenu() }
        lobbyView.onStartGameButtonClicked = { startGame() }
        lobbyView.onOpenSettingsButtonClicked = { enterSettings() }
        lobbyView.init()
    }

    /**
     * Save lobby in backend
     *
     * @param player1
     * @param player2
     * @param lobbyID
     */
    fun startGame(
        // player1: String,
        // player2: String,
        // lobbyID: String,
    ) {
        // TODO: Switch player over to game.
    }

    fun playerJoinedLeftLobby(playerJoined: Boolean) {
        lobbyView.setSecondPlayerConnected(playerJoined)
    }

    /**
     * Change to SettingsPresenter
     */
    private fun enterSettings() {
        PresenterManager.push(StatePresenter(settingsPresenter))
    }

    /**
     * Change to MenuPresenter
     */
    private fun returnToMenu() {
        runBlocking {
            launch {
                try {
                    leaveLobby()
                } catch (e: Exception) {
                    error("Non fatal error: Problem with calling leaveLobby(). Error: " + e.message)
                }
            }
        }
        PresenterManager.pop()
    }

    override fun render() {
        lobbyView.render()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
    }

    override fun dispose() {
        lobbyView.dispose()
    }

    override fun setInputProcessor() {
        lobbyView.setInputProcessor()
    }
}
