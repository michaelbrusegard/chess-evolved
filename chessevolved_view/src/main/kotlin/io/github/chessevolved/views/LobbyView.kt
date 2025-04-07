package io.github.chessevolved.views
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class LobbyView(
    private val lobbyCode: String,
) : IView {
    private lateinit var stage: Stage
    private lateinit var secondPlayerStatusLabel: Label
    private lateinit var startGameButton: TextButton

    var onLeaveButtonClicked: () -> Unit = {}
    var onOpenSettingsButtonClicked: () -> Unit = {}
    var onStartGameButtonClicked: () -> Unit = {}

    override fun init() {
        stage =
            Stage(
                FitViewport(
                    Gdx.graphics.width.toFloat(),
                    Gdx.graphics.height.toFloat(),
                ),
            )

        val copyIconTexture = Texture(Gdx.files.internal("icons/copy-icon.png"))
        val iconDrawable = TextureRegionDrawable(TextureRegion(copyIconTexture))

        val root =
            scene2d.table {
                setFillParent(true)
                defaults().pad(10f).center()

                label("Chess Evolved!") { it.padBottom(20f) }
                row()

                label("Lobby Code: $lobbyCode") { it.padBottom(10f) }
                imageButton {
                    it.size(40f, 40f)
                    it.padLeft(-45f)
                    image(iconDrawable)
                    onClick { copyLobbyCode() }
                }
                row()

                secondPlayerStatusLabel =
                    label("Waiting for second player...") { it.padBottom(10f) }
                row()

                startGameButton =
                    textButton("Start Game") {
                        it.padBottom(20f)
                    }
                row()

                textButton("Lobby Settings") {
                    onClick { onOpenSettingsButtonClicked() }
                }
                row()

                textButton("Leave Lobby") { onClick { onLeaveButtonClicked() } }
            }

        stage.addActor(root)
    }

    fun setSecondPlayerConnected(isConnected: Boolean) {
        if (isConnected) {
            secondPlayerStatusLabel.setText("Second player connected!")
            startGameButton.isDisabled = false
            startGameButton.onClick { onStartGameButtonClicked() }
            startGameButton.color = scene2d.label("tex").color
        } else {
            secondPlayerStatusLabel.setText("Waiting for second player...")
            startGameButton.isDisabled = true
            startGameButton.clearListeners()
            startGameButton.setColor(0f, 0f, 0f, 0.3f)
        }
    }

    private fun copyLobbyCode() {
        val selection = StringSelection(lobbyCode)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
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
}
