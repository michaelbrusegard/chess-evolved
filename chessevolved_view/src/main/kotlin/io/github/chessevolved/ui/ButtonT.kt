package io.github.chessevolved.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import ktx.actors.onClick
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor

@Scene2dDsl
class ButtonT(
    text: String,
    skin: Skin,
    private val defaultColor: Color = Color.WHITE,
    private val hoverColor: Color = Color(0.9f, 0.9f, 0.9f, 1f),
    private val clickColor: Color = Color(0.7f, 0.7f, 0.7f, 1f),
    buttonStyle: String = "default",
) : TextButton(text, skin, buttonStyle) {
    init {
        color = defaultColor

        addListener(
            object : ChangeListener() {
                override fun changed(
                    event: ChangeEvent?,
                    actor: Actor?,
                ) {
                    color =
                        when {
                            isPressed -> clickColor
                            isOver -> hoverColor
                            else -> defaultColor
                        }
                }
            },
        )
    }

    fun setClickListener(onClick: () -> Unit) {
        this.onClick { onClick() }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.button(
    text: String,
    skin: Skin,
    defaultColor: Color = Color.WHITE,
    hoverColor: Color = Color(0.9f, 0.9f, 0.9f, 1f),
    clickColor: Color = Color(0.7f, 0.7f, 0.7f, 1f),
    style: String = "default",
    init: (@Scene2dDsl ButtonT).(S) -> Unit = {},
): ButtonT = actor(ButtonT(text, skin, defaultColor, hoverColor, clickColor, style), init)
