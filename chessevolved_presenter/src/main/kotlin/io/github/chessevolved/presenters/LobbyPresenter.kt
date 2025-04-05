import io.github.chessevolved.PresenterManager
import io.github.chessevolved.presenters.IPresenter
import io.github.chessevolved.presenters.SettingsPresenter
import io.github.chessevolved.presenters.StatePresenter
import io.github.chessevolved.singletons.Lobby.getLobby
import io.github.chessevolved.singletons.Lobby.leaveLobby
import io.github.chessevolved.singletons.Lobby.subscribeToLobbyUpdates
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import io.github.chessevolved.views.LobbyView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        subscribeToLobbyUpdates(::lobbyUpdateHandler)
        runBlocking {
            launch {
                val lobby = getLobby()
                lobbyUpdateHandler(lobby)
            }
        }
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

    private fun playerJoinedLeftLobbyCheck(playerJoined: Boolean) {
        lobbyView.setSecondPlayerConnected(playerJoined)
    }

    private fun lobbyStartedCheck(lobbyStarted: Boolean) {
        if (lobbyStarted) {
            // TODO: Send player to GamePresenter.
        }
    }

    private fun lobbyUpdateHandler(newLobby: SupabaseLobbyHandler.Lobby) {
        println("First check.")
        playerJoinedLeftLobbyCheck(newLobby.second_player)
        lobbyStartedCheck(newLobby.game_started)
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                leaveLobby()
            } catch (e: Exception) {
                error("Non fatal error: Problem with calling leaveLobby(). Error: " + e.message)
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
