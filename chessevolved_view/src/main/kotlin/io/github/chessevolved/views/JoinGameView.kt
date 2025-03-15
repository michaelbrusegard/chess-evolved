package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport

class JoinGameView {
    private var lobbyIdInput: String = ""
    private val stage = Stage(ScreenViewport())

    // UI components
    private val skin =
        Skin()
            .apply {
                // Create default font
                add("default-font", BitmapFont(), BitmapFont::class.java)

                // Create simple label style
                val labelStyle = Label.LabelStyle()
                labelStyle.font = getFont("default-font")
                add("default", labelStyle)

                // Create simple text field style
                val textFieldStyle = TextField.TextFieldStyle()
                textFieldStyle.font = getFont("default-font")
                textFieldStyle.fontColor = Color.WHITE

                // Create a pixel texture for basic UI elements
                val pixelTexture = Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888))
                pixelTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

                val whiteRegion = TextureRegion(pixelTexture)
                whiteRegion.setRegion(0, 0, 1, 1)
                val whiteDrawable = TextureRegionDrawable(whiteRegion)

                textFieldStyle.cursor = whiteDrawable
                textFieldStyle.background = whiteDrawable
                add("default", textFieldStyle)

                // Create simple text button style
                val textButtonStyle = TextButton.TextButtonStyle()
                textButtonStyle.font = getFont("default-font")
                textButtonStyle.up = whiteDrawable
                textButtonStyle.down = whiteDrawable
                add("default", textButtonStyle)
            }

    private val mainTable = Table()
    private val titleLabel = Label("Join Game", skin)
    private val lobbyIdTextField = TextField("", skin)
    private val joinButton = TextButton("Join", skin)
    private val backButton = TextButton("Back to Menu", skin)
    private val statusLabel = Label("", skin)

    // Callback references for the presenter
    private var onJoinButtonPressed: (() -> Unit)? = null
    private var onBackButtonPressed: (() -> Unit)? = null

    init {
        setupUI()
        Gdx.input.inputProcessor = stage
    }

    private fun setupUI() {
        // Configure main table
        mainTable.setFillParent(true)

        // Add elements to table
        mainTable
            .add(titleLabel)
            .padBottom(50f)
            .row()
        mainTable
            .add(Label("Enter Lobby ID:", skin))
            .padBottom(10f)
            .row()
        mainTable
            .add(lobbyIdTextField)
            .width(300f)
            .padBottom(30f)
            .row()

        val buttonTable = Table()
        buttonTable.add(joinButton).width(120f).padRight(20f)
        buttonTable.add(backButton).width(120f)
        mainTable
            .add(buttonTable)
            .padBottom(30f)
            .row()

        mainTable
            .add(statusLabel)
            .width(300f)
            .row()

        // Add listeners
        joinButton.addListener(
            object : ChangeListener() {
                override fun changed(
                    event: ChangeEvent,
                    actor: Actor,
                ) {
                    onJoinButtonPressed?.invoke()
                }
            },
        )

        backButton.addListener(
            object : ChangeListener() {
                override fun changed(
                    event: ChangeEvent,
                    actor: Actor,
                ) {
                    onBackButtonPressed?.invoke()
                }
            },
        )

        lobbyIdTextField.addListener(
            object : ChangeListener() {
                override fun changed(
                    event: ChangeEvent,
                    actor: Actor,
                ) {
                    setLobbyId(lobbyIdTextField.text)
                }
            },
        )

        // Add table to stage
        stage.addActor(mainTable)
    }

    fun setJoinButtonCallback(callback: () -> Unit) {
        onJoinButtonPressed = callback
    }

    fun setBackButtonCallback(callback: () -> Unit) {
        onBackButtonPressed = callback
    }

    fun displayLobbyInput() {
        statusLabel.setText("")
        statusLabel.color = Color.WHITE
    }

    fun showJoinSuccess() {
        statusLabel.setText("Successfully joined lobby!")
        statusLabel.color = Color.GREEN
    }

    fun showJoinError(message: String) {
        statusLabel.setText(message)
        statusLabel.color = Color.RED
    }

    fun getLobbyId(): String = lobbyIdInput

    fun setLobbyId(id: String) {
        lobbyIdInput = id
    }

    fun closeView() {
        // Cleanup resources if needed
    }

    fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    fun resize(
        width: Int,
        height: Int,
    ) {
        stage.viewport.update(width, height, true)
    }

    fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
