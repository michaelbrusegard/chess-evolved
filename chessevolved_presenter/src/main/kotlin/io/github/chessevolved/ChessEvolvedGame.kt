package io.github.chessevolved

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import io.github.chessevolved.singletons.supabase.SupabaseLobbyHandler
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync
import ktx.scene2d.Scene2DSkin

class ChessEvolvedGame : KtxGame<KtxScreen>() {
    private lateinit var batch: SpriteBatch
    private lateinit var skin: Skin
    private lateinit var navigator: Navigator
    private lateinit var assetManager: AssetManager

    init {
        // Initialize lobby-handler early to avoid stutter when trying to join/create lobby for the first time after launching app.
        SupabaseLobbyHandler
    }

    override fun create() {
        KtxAsync.initiate()
        batch = SpriteBatch()

        assetManager = AssetManager()
        assetManager.load("skin/plain-james-ui.json", Skin::class.java)
        assetManager.finishLoading()
        Scene2DSkin.defaultSkin = assetManager.get("skin/plain-james-ui.json", Skin::class.java)

        navigator = Navigator(assetManager)
        navigator.navigateToMenu()
    }

    override fun render() {
        clearScreen(red = 0.5f, green = 0.5f, blue = 0.75f)
        PresenterManager.render(batch)
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        PresenterManager.getCurrent()?.resize(width, height)
        super.resize(width, height)
    }

    override fun dispose() {
        PresenterManager.dispose()
        batch.dispose()
        assetManager.dispose()
    }
}
