package io.github.chessevolved.components
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

private object CurrentCardIdCounter {
    var count = 0
}

class AbilityCardComponent : Component {
    companion object {
        val mapper: ComponentMapper<AbilityCardComponent> =
            ComponentMapper.getFor(AbilityCardComponent::class.java)
    }

    val id = CurrentCardIdCounter.count++
    var selected = false
}
