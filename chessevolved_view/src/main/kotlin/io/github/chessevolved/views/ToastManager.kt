package io.github.chessevolved.views

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

enum class ToastType {
    NORMAL,
    SUCCESS,
    ERROR,
    WARNING,
}

class ToastManager(
    private val stage: Stage,
) {
    private val toastContainer: Table =
        scene2d.table {
            setFillParent(true)
            top()
            padTop(20f)
        }

    init {
        stage.addActor(toastContainer)
    }

    fun showToast(
        message: String,
        type: ToastType = ToastType.NORMAL,
        duration: Float = 3f,
        width: Float = 300f,
    ) {
        toastContainer.clear()

        val toast =
            scene2d.table {
                background("textfield")
                pad(12f)

                when (type) {
                    ToastType.SUCCESS -> {
                        color = Color(0.2f, 0.8f, 0.2f, 0.9f)
                    }
                    ToastType.ERROR -> {
                        color = Color(0.8f, 0.2f, 0.2f, 0.9f)
                    }
                    ToastType.WARNING -> {
                        color = Color(0.9f, 0.9f, 0.2f, 0.9f)
                    }
                    ToastType.NORMAL -> {
                        color = Color(0.3f, 0.3f, 0.3f, 0.9f)
                    }
                }

                label(message, "default") {
                    it.width(width - 24f)
                    it.align(Align.center)
                    setWrap(true)
                }
            }

        toastContainer.add(toast).width(width).pad(10f)

        toast.color.a = 0f
        toast.addAction(
            Actions.sequence(
                Actions.fadeIn(0.5f),
                Actions.delay(duration),
                Actions.fadeOut(0.5f),
                Actions.run { toastContainer.clear() },
            ),
        )
    }

    fun showSuccess(
        message: String,
        duration: Float = 3f,
    ) {
        showToast(message, ToastType.SUCCESS, duration)
    }

    fun showError(
        message: String,
        duration: Float = 3f,
    ) {
        showToast(message, ToastType.ERROR, duration)
    }

    fun showWarning(
        message: String,
        duration: Float = 3f,
    ) {
        showToast(message, ToastType.WARNING, duration)
    }
}
