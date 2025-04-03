import io.github.chessevolved.presenters.IPresenter
import io.github.chessevolved.views.LobbyView

class LobbyPresenter(
    private val lobbyView: LobbyView,
) : IPresenter {
    init {
        lobbyView.onLeaveButtonClicked = { returnToMenu() }
        lobbyView.onStartGameButtonClicked = { startGame() }
        lobbyView.onOpenSettingsButtonClicked = { enterSettings() }
        lobbyView.init()
        lobbyView.setInputProcessor()
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
        // Todo: Finish implementation. temporary using string for player
    }

    fun playerJoinedLeftLobby(playerJoined: Boolean) {
        lobbyView.setSecondPlayerConnected(playerJoined)
    }

    /**
     * Change to SettingsPresenter
     */
    fun enterSettings() {
        // TODO: Wait for ScenePresenterStateManager
    }

    /**
     * Change to MenuPresenter
     */
    fun returnToMenu() {
        // TODO: Wait for ScenePresenterStateManager
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
