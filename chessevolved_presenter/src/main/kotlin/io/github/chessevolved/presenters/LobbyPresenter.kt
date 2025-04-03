import io.github.chessevolved.ScenePresenterStateManager
import io.github.chessevolved.presenters.IPresenter
import io.github.chessevolved.presenters.SettingsPresenter
import io.github.chessevolved.presenters.StatePresenter
import io.github.chessevolved.views.LobbyView

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
        ScenePresenterStateManager.push(StatePresenter(settingsPresenter))
    }

    /**
     * Change to MenuPresenter
     */
    private fun returnToMenu() {
        ScenePresenterStateManager.pop()
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
