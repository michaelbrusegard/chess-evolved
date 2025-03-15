package io.github.chessevolved.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor

@Scene2dDsl
class TextFieldT(
    text: String = "",
    skin: Skin,
    textFieldStyle: String = "default",
    private val focusedColor: Color = Color(0.9f, 0.9f, 1f, 1f),
    private val unfocusedColor: Color = Color(0.8f, 0.8f, 0.8f, 1f),
) : TextField(text, skin, textFieldStyle) {
    init {
        color = unfocusedColor
        setTextFieldListener { textField, c ->
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        color = if (hasKeyboardFocus()) focusedColor else unfocusedColor
    }

    fun onTextChange(listener: (String) -> Unit) {
        setTextFieldListener { _, _ -> listener(text) }
    }
}

@Scene2dDsl
fun <S> KWidget<S>.customTextField(
    text: String = "",
    skin: Skin,
    style: String = "default",
    focusedColor: Color = Color(0.9f, 0.9f, 1f, 1f),
    unfocusedColor: Color = Color(0.8f, 0.8f, 0.8f, 1f),
    init: (@Scene2dDsl TextFieldT).(S) -> Unit = {},
): TextFieldT = actor(TextFieldT(text, skin, style, focusedColor, unfocusedColor), init)
