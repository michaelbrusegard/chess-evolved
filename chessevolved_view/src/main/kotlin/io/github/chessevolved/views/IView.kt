package io.github.chessevolved.views

interface IView {
    fun init()

    fun render()

    fun resize(
        width: Int,
        height: Int,
    )

    fun dispose()
}
