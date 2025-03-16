package io.github.chessevolved.presenters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import io.github.chessevolved.singletons.GameSettings
import io.github.chessevolved.views.IView
import io.github.chessevolved.views.IView

class SettingsPresenter(
    givenView: IView,
) : IPresenter {
class SettingsPresenter(
    givenView: IView,
) : IPresenter {
    // TODO: wait for implementation of ScenePresenterStateManager
    private val gameSettings = GameSettings
    // val presenterManager = ScenePresenterStateManager

    private val view = givenView
    private val buttonTexture: Texture = Texture("buttons/placeholderButton.png")
    private val buttonSprite: Sprite = Sprite(buttonTexture)

    init {
        buttonSprite.setPosition(
            (Gdx.graphics.width/2 - buttonSprite.width/2),
            50f
        )
    }

    private val view = givenView
    private val buttonTexture: Texture = Texture("buttons/placeholderButton.png")
    private val buttonSprite: Sprite = Sprite(buttonTexture)

    init {
        buttonSprite.setPosition(
            (Gdx.graphics.width/2 - buttonSprite.width/2),
            50f
        )
    }

    /**
     * Applies the chosen game settings
     *
     * @param fowSetting Boolean for Fog of War
     * @param sizeSetting Int for size of chessboard
     */
    fun onApply(
        fowSetting: Boolean,
        sizeSetting: Int,
    ) {
        // TODO: Consider if game settings should be applied manually or automatically
        gameSettings.setFOW(fowSetting)

        // TODO: validate max/min board size here?
        // TODO: validate max/min board size here?
        gameSettings.setBoardSize(sizeSetting)
    }

    /**
     * Retrieves the current game settings
     *
     * @return Current settings as a Map
     */
    fun getCurrentSettings(): Map<String, Any> =
        mapOf(
            "FogOfWar" to gameSettings.isFOWEnabled(),
            "BoardSize" to gameSettings.getBoardSize(),
        )

    /**
     *  Switch to LobbyPresenter
     */
    fun returnToLobby() {
        // TODO: wait for implementation of ScenePresenterStateManager
    }

    override fun render() {
        // Required by IPresenter
        view.beginBatch()
        view.render(buttonSprite)
        view.endBatch()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
    }

    override fun dispose() {
    }
}
