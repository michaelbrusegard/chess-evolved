package io.github.chessevolved.presenters

interface IPresenter {
    // Essential Common handling should be defined here.
    fun render()

    fun resize(
        width: Int,
        height: Int,
    )

    fun dispose()
}
