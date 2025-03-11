import io.github.chessevolved.presenters.IPresenter

class LobbyPresenter : IPresenter {

    /**
     * Save lobby in backend
     *
     * @param player1
     * @param player2
     * @param lobbyID
     */
    fun saveLobby(player1: String, player2: String, lobbyID: String) {
        //Todo: Finish implementation. temporary using string for player
    }

    /**
     * Change to SettingsPresenter
     */
    fun enterSettings() {
        //TODO: Wait for ScenePresenterStateManager
    }

    /**
     * Change to MenuPresenter
     */
    fun returnToMenu() {
        //TODO: Wait for ScenePresenterStateManager
    }
}
