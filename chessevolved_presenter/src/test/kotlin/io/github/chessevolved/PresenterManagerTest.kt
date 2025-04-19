package io.github.chessevolved

import com.badlogic.gdx.assets.AssetManager
import io.github.chessevolved.presenters.IPresenter
import io.github.chessevolved.presenters.MockPresenter
import io.github.chessevolved.views.LobbyView
import java.util.ArrayDeque
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PresenterManagerTest {
    private var presenters = ArrayDeque<IPresenter>()
    private val presenter = MockPresenter(LobbyView(""), Navigator(AssetManager()))

    @Test
    fun push() {
        presenters.push(presenter)
        PresenterManager.push(presenter)
        assertEquals(presenters.peekFirst(), PresenterManager.getCurrent())
        presenters = ArrayDeque()
    }

    @Test
    fun pop() {
        presenters.push(presenter)
        PresenterManager.push(presenter)
        presenters.pop()
        PresenterManager.pop()
        assertEquals(presenters.peekFirst(), PresenterManager.getCurrent())
        presenters = ArrayDeque()
    }

    @Test
    fun set() {
        presenters.push(presenter)
        PresenterManager.set(presenter)
        assertEquals(presenters.peekFirst(), PresenterManager.getCurrent())
        presenters = ArrayDeque()
    }

    @Test
    fun isEmpty() {
        while (!PresenterManager.isEmpty()) {
            PresenterManager.pop()
        }
        assertTrue { PresenterManager.isEmpty() }
    }
}
