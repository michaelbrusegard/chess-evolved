package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite

class AbilityComponent (
    private var activeAbilities: ArrayList<String>,
    private var activeAbilitiesSprites: ArrayList<Sprite>,
    private var passiveAbilities: ArrayList<String>,
    private var passiveAbilitiesSprites: ArrayList<Sprite>
) : Component {

    /**
     * @param type - 0 to add an active ability, 1 to add a passive ability
     * @param ability - ability name as a string in lowercase
     */
    fun addAbility(
        type: Int,
        ability: String
    ) {
        if (type == 0) {
            activeAbilities.add(ability)
        }
        else if (type == 1) {
            passiveAbilities.add(ability)
        }
    }

    fun getActiveAbilities(): ArrayList<String> {
        return activeAbilities
    }

    fun getPassiveAbilities(): ArrayList<String> {
        return passiveAbilities
    }
}
