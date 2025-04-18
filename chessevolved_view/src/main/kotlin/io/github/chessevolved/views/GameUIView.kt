package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.onClick
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textField
import kotlin.math.min

class GameUIView(
    /**
     * This variable can be used to make sure the ui doesn't overlap the game-board.
     */
    private val gameViewport: Viewport,
    private val isWhitePlayer: Boolean,
) : IView {
    private lateinit var stage: Stage
    private val blackColor = Color(0.37f, 0.5f, 0.6f, 0.8f)
    private val whiteColor = Color(1f, 1f, 1f, 0.8f)
    private var amountOfAbilityCards = 0
    private var sizeOfAbilityCards = min(100, (Gdx.graphics.width - 10) / (amountOfAbilityCards + 1))
    private val abilityCards = HashMap<Int, ImageButton>()
    private lateinit var abilityCardInventory: Table
    private lateinit var abilityPickerWindow: Table
    private lateinit var blackTimer: TextField
    private lateinit var whiteTimer: TextField

    override fun init() {
        stage = Stage(gameViewport)

        val blackInfoBox =
            scene2d.table {
                textField(if (isWhitePlayer) "Black: Opponent" else "Black: You") {
                    color = blackColor
                    isDisabled = true
                }.cell(growX = true)

                blackTimer =
                    textField("Time: 10:00") {
                        color = blackColor
                        isDisabled = true
                    }
            }

        abilityPickerWindow =
            scene2d.table {
                defaults().width(100f).height(100f).pad(4f)
                imageButton()
                imageButton()
                imageButton()
            }

        abilityCardInventory =
            scene2d.table {
                // This label is here to always keep the height of the abilityCardInventory the same.
                label("").cell(expandY = true, height = 100f, width = 0f)
                defaults().width(sizeOfAbilityCards.toFloat()).height(sizeOfAbilityCards.toFloat())
            }

        val whiteInfoBox =
            scene2d.table {
                textField(if (isWhitePlayer) "White: You" else "White: Opponent") {
                    color = whiteColor
                    isDisabled = true
                }.cell(growX = true)

                whiteTimer =
                    textField("Time: 10:00") {
                        color = whiteColor
                        isDisabled = true
                    }
            }

        val root =
            scene2d.table {
                // setDebug(true, true)
                setFillParent(true)
                add(blackInfoBox).growX().expandY().top()
                row()
                add(abilityPickerWindow.bottom().padBottom(150f)).expandY().height(500f)
                row()
                add(abilityCardInventory)
                    .growX()
                    .growY()
                    .expandY()
                    .bottom()
                row()
                add(whiteInfoBox).growX()
            }

        stage.addActor(root)
    }

    data class ThreeAbilities(
        val ability1Texture: Texture,
        val ability2Texture: Texture,
        val ability3Texture: Texture,
        val onAbilityPickedListener: (abilityPicked: Int) -> Unit,
    )

    fun promptPickAbility(abilities: ThreeAbilities) {
        abilityPickerWindow.reset()
        abilityPickerWindow.isVisible = true
        abilityPickerWindow.add(
            scene2d.imageButton { onClick { onAbilityPicked(abilities.onAbilityPickedListener, 1) } },
        )
        abilityPickerWindow.add(
            scene2d.imageButton { onClick { onAbilityPicked(abilities.onAbilityPickedListener, 2) } },
        )
        abilityPickerWindow.add(
            scene2d.imageButton { onClick { onAbilityPicked(abilities.onAbilityPickedListener, 3) } },
        )
    }

    private fun onAbilityPicked(
        onAbilityPickedListener: (abilityPicked: Int) -> Unit,
        abilityPicked: Int,
    ) {
        abilityPickerWindow.isVisible = false
        onAbilityPickedListener.invoke(abilityPicked)
    }

    /**
     * @param abilityId corresponds to the id of the ability, not the ability-type. abilityId will be used
     * to remove the ability from the inventory as well.
     */
    fun addAbilityCardToInventory(
        abilityTexture: Texture,
        onAbilityUsed: (abilityId: Int) -> Unit,
        abilityId: Int,
    ) {
        amountOfAbilityCards++
        sizeOfAbilityCards = min(100, (Gdx.graphics.width - 10) / amountOfAbilityCards + 1)

        var abilityCard =
            scene2d.imageButton {
                image(TextureRegionDrawable(TextureRegion(abilityTexture)))
                onClick {
                    onAbilityUsed(abilityId)
                }
            }
        abilityCardInventory.add(abilityCard)

        abilityCards[abilityId] = abilityCard

        abilityCardInventory.cells.forEach {
            if (it.expandY != 1) {
                it.width(sizeOfAbilityCards.toFloat()).height(sizeOfAbilityCards.toFloat())
            }
        }
        // abilityCardInventory.defaults().width(sizeOfAbilityCards.toFloat()).height(sizeOfAbilityCards.toFloat())
    }

    fun removeAbilityCardFromInventory(abilityId: Int) {
        abilityCards[abilityId]?.remove()
    }

    fun updateWhiteTimer(time: Int) {
        whiteTimer.text = "Time: $time"
    }

    fun updateBlackTimer(time: Int) {
        blackTimer.text = "Time: $time"
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
        throw IllegalAccessError("setInputProcessor is not allowed for GameUIView. Call GameView's setInputProcessor instead.")
        // Gdx.input.inputProcessor = stage
    }

    fun getStage() = stage
}
