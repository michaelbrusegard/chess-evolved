package io.github.chess2.View

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import io.github.chess2.Chess2Game
import ktx.actors.stage
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import ktx.scene2d.table
import ktx.scene2d.textButton

class GameScreen(private val game: Chess2Game) : KtxScreen {

    private val stage = stage()

    init {
        println("GameScreen: Initialized!")
        Gdx.input.inputProcessor = stage

        stage.actors {
            table {
                setFillParent(true)
                textButton("Pause", skin = Scene2DSkin.defaultSkin).apply {
                    addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            game.setScreen<PauseScreen>()
                        }
                    })
                }
                row()
                textButton("Back To Menu", skin = Scene2DSkin.defaultSkin).apply {
                    addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            game.setScreen<MenuScreen>()
                        }
                    })
                }
            }
        }
    }

    override fun render(delta: Float) {
        clearScreen(0.1f, 0.1f, 0.1f)  // Plain dark background
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}
