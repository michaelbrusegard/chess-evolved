package io.github.chessevolved

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.chessevolved.ui.ButtonT
import io.github.chessevolved.ui.TextFieldT
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.table

class ChessEvolvedGame : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("comic/skin/comic-ui.json")) // Load the skin

    init {
        Scene2DSkin.defaultSkin = skin
        // Set up the UI using your custom components
        val textField =
            TextFieldT(
                text = "",
                skin = skin,
                focusedColor = skin.getColor("white"),
                unfocusedColor = skin.getColor("gray"),
            )

        val joinButton =
            ButtonT(
                text = "Join Game",
                skin = skin,
                defaultColor = skin.getColor("white"),
                hoverColor = skin.getColor("gray"),
                clickColor = skin.getColor("black"),
            )

        val backButton =
            ButtonT(
                text = "Back",
                skin = skin,
                defaultColor = skin.getColor("white"),
                hoverColor = skin.getColor("gray"),
                clickColor = skin.getColor("black"),
            )

        // Set up button callbacks
        joinButton.setClickListener {
            println("Join button clicked! Text: ${textField.text}")
        }

        backButton.setClickListener {
            println("Back button clicked!")
        }

        // Add components to the stage using a table layout
        stage.addActor(
            scene2d.table {
                setFillParent(true)
                pad(20f)

                add("Enter Game Code:").row()
                add(textField).width(300f).pad(10f).row()
                add(joinButton).width(200f).pad(10f).row()
                add(backButton).width(200f).pad(10f).row()

                center()
            },
        )

        // Set the input processor to the stage
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.1f, green = 0.1f, blue = 0.23f)
        stage.act(delta)
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
        skin.dispose()
    }
}
