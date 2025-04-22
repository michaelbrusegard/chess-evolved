package io.github.chessevolved.presenters

import io.github.chessevolved.Navigator
import io.github.chessevolved.views.MenuView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class MenuPresenterTest {
    private lateinit var view: MenuView
    private lateinit var navigator: Navigator
    private lateinit var presenter: MenuPresenter

    /**
     * Sets up mocks and initializes the presenter before each test.
     */
    @BeforeEach
    fun setUp() {
        view = mock()
        navigator = mock()
        presenter = MenuPresenter(view, navigator)
    }

    /**
     * Verifies that listeners are set up.
     */
    @Test
    fun `init should set button listeners and initialize view`() {
        verify(view).onCreateLobbyButtonClicked = any()
        verify(view).onJoinGameButtonClicked = any()
        verify(view).init()
    }

    /**
     * Simulates a "Join Game" button click and checks if navigation is triggered.
     */
    @Test
    fun `join game button click should navigate to join game`() {
        val captor = argumentCaptor<() -> Unit>()
        verify(view).onJoinGameButtonClicked = captor.capture()
        captor.firstValue.invoke()
        verify(navigator).navigateToJoinGame()
    }
}
