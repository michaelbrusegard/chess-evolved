package io.github.chessevolved.presenters

/**
 * Interface for all presenters.
 * Presenters connect models to views and contain application logic.
 */
interface IPresenter {
    /**
     * Updates and renders the presenter's content for the current frame.
     * Called on each render cycle.
     */
    fun render()

    /**
     * Handles screen size changes and updates the presenter accordingly.
     *
     * @param width The new screen width in pixels
     * @param height The new screen height in pixels
     */
    fun resize(
        width: Int,
        height: Int,
    )

    /**
     * Releases resources used by the presenter.
     * Should be called when the presenter is no longer needed to prevent memory leaks.
     */
    fun dispose()
}
