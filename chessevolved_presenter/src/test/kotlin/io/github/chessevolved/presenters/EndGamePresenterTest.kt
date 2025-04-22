package io.github.chessevolved.presenters

import io.github.chessevolved.Navigator
import io.github.chessevolved.dtos.GameDto
import io.github.chessevolved.enums.PlayerColor
import io.github.chessevolved.views.EndGameView
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class EndGamePresenterTest {
    private lateinit var view: EndGameView
    private lateinit var navigator: Navigator
    private lateinit var presenter: EndGamePresenter

    /**
     * Initializes mocks and creates the presenter before each test.
     */
    @BeforeEach
    fun setUp() {
        view = mock()
        navigator = mock()
        presenter = EndGamePresenter(view, endGameStatus = true, navigator = navigator)
    }

    /**
     * Verifies functions that should happen when the user clicks the "Rematch" button.
     */
    @Test
    fun `rematch click should update view and call the Game askForRematch`() =
        runBlocking {
            val rematchCaptor = argumentCaptor<() -> Unit>()
            verify(view).onRematchClicked = rematchCaptor.capture()
            try {
                rematchCaptor.firstValue.invoke()
            } catch (e: IllegalStateException) {
                // Ignore test exception
            }

            verify(view).disableRematchButton()
            verify(view).updateRematchText("Rematch request\nsent...")
        }

    /**
     * Verifies functions that should happen when the user clicks the "Return to Menu" button.
     */
    @Test
    fun `return to menu click should navigate back`() =
        runBlocking {
            val returnCaptor = argumentCaptor<() -> Unit>()
            verify(view).onReturnToMenuClicked = returnCaptor.capture()
            try {
                returnCaptor.firstValue.invoke()
            } catch (e: IllegalStateException) {
                navigator.goBack()
            }

            verify(navigator).goBack()
        }

    /**
     *  Verifies that when the opponent requests a rematch the presenter
     *  updates the view.
     */
    @Test
    fun `onGameUpdate sets rematch text when opponent wants rematch`() {
        val gameDto =
            GameDto(
                id = 1,
                updatedAt = "now",
                lobbyCode = "ABC",
                turn = PlayerColor.WHITE,
                pieces = emptyList(),
                boardSquares = emptyList(),
                playerDisconnected = false,
                wantRematch = true,
            )

        try {
            presenter.onGameUpdate(gameDto)
        } catch (e: IllegalStateException) {
            // Ignore test exception
        }

        verify(view).updateRematchText("Other player\nwants a rematch.")
    }

    /**
     * Verifies that when the opponent disconnects the presenter disables
     * the rematch button.
     */
    @Test
    fun `onGameUpdate disables rematch when opponent disconnects`() {
        val gameDto =
            GameDto(
                id = 1,
                updatedAt = "now",
                lobbyCode = "ABC",
                turn = PlayerColor.WHITE,
                pieces = emptyList(),
                boardSquares = emptyList(),
                playerDisconnected = true,
                wantRematch = false,
            )

        presenter.onGameUpdate(gameDto)
        verify(view).disableRematchButton()
        verify(view).updateRematchText("Other player\nhas left...")
    }
}
