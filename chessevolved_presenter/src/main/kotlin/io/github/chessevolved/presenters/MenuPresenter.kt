package io.github.chessevolved.presenters

import ScenePresenterStateManage
import State
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.github.chessevolved.views.AndroidView
import io.github.chessevolved.views.IView
import io.github.chessevolved.views.MenuView

class MenuPresenter(
    givenView: IView,
    //menuView: MenuView,
) : State() {
    val view: IView = givenView

    override fun render(sb: SpriteBatch) {
        sb.begin()
        println("Menu: Rendering menu")
        sb.end()
    }

    fun enterJoinGame() {
        // TODO: Append JoinGamePresenter to the Stack.
    }

    fun enterCreateGame() {
        // TODO: Append CreateGamePresenter to the stack.
    }

    override fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ScenePresenterStateManage.pop()
        }
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun dispose() {
        println("Disposing menu")
    }
}
