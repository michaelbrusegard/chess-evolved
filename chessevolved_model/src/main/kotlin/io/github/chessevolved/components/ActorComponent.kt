package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.scenes.scene2d.Actor

class ActorComponent(
    componentActor: Actor,
) : Component {
    companion object {
        val mapper: ComponentMapper<ActorComponent> =
            ComponentMapper.getFor(ActorComponent::class.java)
    }

    val actor: Actor = componentActor
}
