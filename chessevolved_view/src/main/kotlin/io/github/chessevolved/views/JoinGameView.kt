
package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

interface JoinGameView {
    fun displayLobbyInput()

    fun showJoinSuccess()

    fun showJoinError(message: String)

    fun getLobbyId(): String

    fun closeView()

    fun render()

    fun resize(
        width: Int,
        height: Int,
    )

    fun handleInput(
        screenX: Int,
        screenY: Int,
    )

    fun handleKeyTyped(character: Char)
}

class JoinGameViewImpl(
    private val onJoinButtonClicked: () -> Unit,
    private val onBackButtonClicked: () -> Unit,
) : JoinGameView {
    private var lobbyIdInput: String = ""
    private var statusMessage: String = ""
    private var statusColor: Color = Color.WHITE

    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    private val camera =
        com.badlogic.gdx.graphics
            .OrthographicCamera()
    private val viewport: Viewport

    private val inputField = Rectangle()
    private val joinButton = Rectangle()
    private val backButton = Rectangle()
    private var inputFieldActive = false

    private val glyphLayout = GlyphLayout()

    init {
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), camera)

        font.data.setScale(1.5f)

        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        inputField.width = screenWidth * 0.6f
        inputField.height = 50f
        inputField.x = (screenWidth - inputField.width) / 2
        inputField.y = screenHeight * 0.6f

        joinButton.width = 150f
        joinButton.height = 50f
        joinButton.x = screenWidth * 0.6f
        joinButton.y = screenHeight * 0.4f

        backButton.width = 150f
        backButton.height = 50f
        backButton.x = screenWidth * 0.3f - backButton.width
        backButton.y = screenHeight * 0.4f
    }

    override fun displayLobbyInput() {
        statusMessage = ""
        lobbyIdInput = ""
        inputFieldActive = false
    }

    override fun showJoinSuccess() {
        statusMessage = "Successfully joined lobby!"
        statusColor = Color.GREEN
    }

    override fun showJoinError(message: String) {
        statusMessage = message
        statusColor = Color.RED
    }

    override fun getLobbyId(): String = lobbyIdInput

    override fun closeView() {
        batch.dispose()
        shapeRenderer.dispose()
        font.dispose()
    }

    override fun render() {
        camera.update()
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        if (inputFieldActive) {
            shapeRenderer.setColor(0.3f, 0.3f, 0.7f, 1f)
        } else {
            shapeRenderer.setColor(0.2f, 0.2f, 0.5f, 1f)
        }
        shapeRenderer.rect(inputField.x, inputField.y, inputField.width, inputField.height)

        shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 1f)
        shapeRenderer.rect(joinButton.x, joinButton.y, joinButton.width, joinButton.height)

        shapeRenderer.setColor(0.6f, 0.2f, 0.2f, 1f)
        shapeRenderer.rect(backButton.x, backButton.y, backButton.width, backButton.height)

        shapeRenderer.end()

        batch.begin()

        font.setColor(Color.WHITE)
        glyphLayout.setText(font, "Join Game")
        font.draw(
            batch,
            "Join Game",
            (Gdx.graphics.width - glyphLayout.width) / 2,
            Gdx.graphics.height * 0.8f,
        )

        font.draw(
            batch,
            "Enter Lobby ID:",
            inputField.x,
            inputField.y + inputField.height + 30,
        )

        font.draw(
            batch,
            lobbyIdInput,
            inputField.x + 10,
            inputField.y + inputField.height / 2 + font.capHeight / 2,
        )

        glyphLayout.setText(font, "Join")
        font.draw(
            batch,
            "Join",
            joinButton.x + (joinButton.width - glyphLayout.width) / 2,
            joinButton.y + joinButton.height / 2 + font.capHeight / 2,
        )

        glyphLayout.setText(font, "Back")
        font.draw(
            batch,
            "Back",
            backButton.x + (backButton.width - glyphLayout.width) / 2,
            backButton.y + backButton.height / 2 + font.capHeight / 2,
        )

        if (statusMessage.isNotEmpty()) {
            font.setColor(statusColor)
            glyphLayout.setText(font, statusMessage)
            font.draw(
                batch,
                statusMessage,
                (Gdx.graphics.width - glyphLayout.width) / 2,
                Gdx.graphics.height * 0.3f,
            )
            font.setColor(Color.WHITE)
        }

        batch.end()
    }

    override fun resize(
        width: Int,
        height: Int,
    ) {
        viewport.update(width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
    }

    override fun handleInput(
        screenX: Int,
        screenY: Int,
    ) {
        val worldCoords =
            viewport.unproject(
                com.badlogic.gdx.math
                    .Vector3(screenX.toFloat(), screenY.toFloat(), 0f),
            )

        if (inputField.contains(worldCoords.x, worldCoords.y)) {
            inputFieldActive = true
        } else {
            inputFieldActive = false
        }

        if (joinButton.contains(worldCoords.x, worldCoords.y)) {
            onJoinButtonClicked()
        }

        if (backButton.contains(worldCoords.x, worldCoords.y)) {
            onBackButtonClicked()
        }
    }

    override fun handleKeyTyped(character: Char) {
        if (inputFieldActive) {
            if (character == '\b' && lobbyIdInput.isNotEmpty()) {
                lobbyIdInput = lobbyIdInput.dropLast(1)
            } else if (character.isLetterOrDigit() || character == '-') {
                lobbyIdInput += character
            }
        }
    }
}
