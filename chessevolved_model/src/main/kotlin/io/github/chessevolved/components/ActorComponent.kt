package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.Actor

class ActorComponent(componentActor: Actor): Component {
    val actor: Actor = componentActor
}
