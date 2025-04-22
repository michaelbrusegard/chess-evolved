package io.github.chessevolved.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.onClick
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.textField
import kotlin.math.min

class GameUIView(
    /**
     * This variable can be used to make sure the ui doesn't overlap the game-board.
     */
    private val gameViewport: Viewport,
    private val isWhitePlayer: Boolean,
    private val onPickAbilityCardButtonClicked: () -> Unit,
) : IView {
    private lateinit var stage: Stage
    private val blackColor = Color(0.37f, 0.5f, 0.6f, 0.8f)
    private val whiteColor = Color(1f, 1f, 1f, 0.8f)
    private val abilityCards = HashMap<Int, ImageButton>()
    private var sizeOfAbilityCards = min(100, (Gdx.graphics.width - 10) / (abilityCards.size + 1))
    private var promptedAmountOfPickableAbilities = 0
    private lateinit var abilityCardInventory: Table
    private lateinit var abilityPickerWindow: Table
    private lateinit var blackTimer: TextField
    private lateinit var whiteTimer: TextField
    private lateinit var abilityDescriptionLabel: Label
    private lateinit var abilityInfoTable: Table
    private lateinit var pickAbilityButton: TextButton

    data class AbilityCardInformation(
        val texture: Texture? = Texture(Gdx.files.internal("pieces/pawn-white.png")),
        val id: Int,
    )

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
                row()
            }

        val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
        bgPixmap.setColor(Color.WHITE)
        bgPixmap.fill()
        val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))

        abilityInfoTable =
            scene2d.table {
                color = blackColor
                label("Ability Information:") {
                    setAlignment(1)
                }.cell(growX = true, colspan = 2)
                row()
                abilityDescriptionLabel =
                    label("") {
                        wrap = true
                        setAlignment(1)
                    }.cell(growX = true, colspan = 2)
                setBackground(textureRegionDrawableBg)
            }

        bgPixmap.dispose()

        abilityPickerWindow =
            scene2d.table {
                defaults().width(100f).height(100f).pad(4f)
                isVisible = false
            }

        abilityCardInventory =
            scene2d.table {
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
                setFillParent(true)
                add(blackInfoBox)
                    .growX()
                    .top()
                    .padBottom(10f)
                row()
                add(abilityInfoTable.top())
                    .growX()
                    .expandY()
                    .top()
                    .height(100f)
                row()
                add(abilityPickerWindow).height(200f)
                row()
                pickAbilityButton =
                    textButton("Select Ability") {
                        onClick { onPickAbilityCardButtonClicked() }
                        isVisible = false
                    }.cell(padTop = -10f)
                row()
                add(abilityCardInventory)
                    .growX()
                    .growY()
                    .expandY()
                    .bottom()
                    .height(120f)
                row()
                add(whiteInfoBox).growX()
            }

        stage.addActor(root)
    }

    fun promptPickAbility(
        abilityCards: Set<AbilityCardInformation>,
        onAbilityPickedListener: (idOfAbilityClicked: Int) -> Unit,
    ) {
        if (promptedAmountOfPickableAbilities == abilityCards.size) return
        promptedAmountOfPickableAbilities = abilityCards.size
        abilityPickerWindow.reset()
        abilityPickerWindow.isVisible = true
        pickAbilityButton.isVisible = true

        abilityCards.forEach {
            val imgButtonStyle = ImageButtonStyle()
            val id = it.id
            val texture = it.texture
            abilityPickerWindow
                .add(
                    scene2d.imageButton {
                        style = imgButtonStyle
                        onClick { onAbilityPickedListener(id) }
                        style.up = TextureRegionDrawable(TextureRegion(texture))
                        style.down = TextureRegionDrawable(TextureRegion(texture))
                        style.over = TextureRegionDrawable(TextureRegion(texture)).tint(Color(0.5f, 0.5f, 0.5f, 1f))
                    },
                ).width(100f)
                .height(100f)
        }
    }

    fun hidePromptPickAbility() {
        if (!abilityPickerWindow.isVisible) return
        promptedAmountOfPickableAbilities = 0
        abilityPickerWindow.isVisible = false
        pickAbilityButton.isVisible = false
    }

    fun updateCardsInInventory(
        abilityCards: Set<AbilityCardInformation>,
        onAbilityUsed: (abilityId: Int) -> Unit,
    ) {
        // Avoid updating inventory if inventory haven't lost/gained cards.
        if (abilityCards.size == this.abilityCards.size) return

        abilityCardInventory.clear()
        this.abilityCards.clear()
        abilityCards.forEach {
            addAbilityCardToInventory(it, onAbilityUsed)
        }
    }

    private fun addAbilityCardToInventory(
        abilityCardInformation: AbilityCardInformation,
        onAbilityUsed: (idOfAbilityClicked: Int) -> Unit,
    ) {
        sizeOfAbilityCards = min(100, (Gdx.graphics.width - 10) / (abilityCards.size + 1))

        val imgButtonStyle = ImageButtonStyle()
        val abilityCard =
            scene2d.imageButton {
                style = imgButtonStyle
                style.up = TextureRegionDrawable(TextureRegion(abilityCardInformation.texture))
                style.down = TextureRegionDrawable(TextureRegion(abilityCardInformation.texture))
                style.over = TextureRegionDrawable(TextureRegion(abilityCardInformation.texture)).tint(Color(0.5f, 0.5f, 0.5f, 1f))
                onClick {
                    onAbilityUsed(abilityCardInformation.id)
                }
            }
        abilityCardInventory.add(abilityCard)

        abilityCards[abilityCardInformation.id] = abilityCard

        abilityCardInventory.cells.forEach {
            it.width(sizeOfAbilityCards.toFloat()).height(sizeOfAbilityCards.toFloat())
        }
    }

    fun selectCardFromInventory(
        abilityInformation: String,
        abilityCardId: Int,
    ) {
        abilityDescriptionLabel.setText(abilityInformation)
        abilityInfoTable.isVisible = abilityInformation != ""

        abilityCards.forEach {
            it.value.width = sizeOfAbilityCards.toFloat()
            it.value.height = sizeOfAbilityCards.toFloat()
        }

        abilityCards[abilityCardId]?.width = sizeOfAbilityCards.toFloat() + 20f
        abilityCards[abilityCardId]?.height = sizeOfAbilityCards.toFloat() + 20f
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
