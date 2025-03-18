package io.github.chessevolved.views

/**
 * Interface for all views.
 * Views are responsible for rendering UI elements and handling user input.
 * All methods will be called by or passed through the presenter.
 */
interface IView {
    /**
     * Initializes the view with necessary components and resources.
     * This should be called before first use in the presenter.
     */
    fun init()

    /**
     * Renders the view content in the current frame.
     * Called on each render cycle.
     */
    fun render()

    /**
     * Handles screen size changes.
     *
     * @param width The new screen width in pixels
     * @param height The new screen height in pixels
     */
    fun resize(
        width: Int,
        height: Int,
    )

    /**
     * Releases resources used by the view.
     * Should be called when the view is no longer needed to prevent memory leaks.
     */
    fun dispose()
}
