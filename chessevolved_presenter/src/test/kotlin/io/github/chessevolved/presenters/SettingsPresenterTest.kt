package io.github.chessevolved.presenters

import io.github.chessevolved.Navigator
import io.github.chessevolved.views.SettingsView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SettingsPresenterTest {
    private lateinit var settingsView: SettingsView
    private lateinit var navigator: Navigator
    private lateinit var presenter: SettingsPresenter

    /**
     * Sets up mocks and initializes the presenter before each test.
     */
    @BeforeEach
    fun setUp() {
        settingsView = mock()
        navigator = mock()
        presenter = SettingsPresenter(settingsView, navigator)
    }

    /**
     * Verifies that the view is initialized and existing settings are loaded.
     */
    @Test
    fun `init should initialize the view and load existing settings`() {
        verify(settingsView).init()
        verify(settingsView).setExistingSettings(any())
        verify(settingsView).setInitialValues(any())
    }

    /**
     * Verifies that the "cancel" button click should just navigate back.
     */
    @Test
    fun `cancel button click should navigate back`() {
        val captor = argumentCaptor<() -> Unit>()
        verify(settingsView).onCancelClicked = captor.capture()
        captor.firstValue.invoke()
        verify(navigator).goBack()
    }
}
