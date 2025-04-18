package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

class MenuView : IView {
    private lateinit var stage: Stage

    var onCreateLobbyButtonClicked: () -> Unit = {}
    var onJoinGameButtonClicked: () -> Unit = {}

    private lateinit var toastManager: ToastManager

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport

    private val logoTexture = Texture("customUI/chessEvolvedLogo.png")

    override fun init() {
        val screenRatio = Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat()

        camera = OrthographicCamera()
        viewport = FitViewport(500f, 500f / screenRatio, camera)

        stage = Stage(viewport)

        println("ScreenWidth: ${Gdx.graphics.width}, ScreenHeight: ${Gdx.graphics.height}")

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f).center()

                image(logoTexture) {
                    it.padBottom(20f)

                    val ratio = logoTexture.width.toFloat() / logoTexture.height.toFloat()
                    it.size(300f, 300f / ratio)
                }
                row()

                textButton("Create Lobby") {
                    it.padBottom(5f).width(200f)
                    onClick { onCreateLobbyButtonClicked() }
                }
                row()

                textButton("Join Game") {
                    it.width(200f)
                    onClick { onJoinGameButtonClicked() }
                }
                row()
                textButton("Exit") { onClick { Gdx.app.exit() } }
            }

        toastManager = ToastManager(stage)
        stage.addActor(root)
    }

    override fun render() {
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun setInputProcessor() {
        Gdx.input.inputProcessor = stage
    }

    fun showCreateGameError(message: String) {
        toastManager.showError("Error: $message")
    }
}
