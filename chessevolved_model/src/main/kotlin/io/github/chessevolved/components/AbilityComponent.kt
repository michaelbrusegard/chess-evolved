package io.github.chessevolved.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import ktx.assets.toInternalFile

class AbilityComponent (
    private var activeAbilities: ArrayList<String>,
    private var passiveAbilities: ArrayList<String>,
    private var abilityDescriptions: HashMap<String, String>
) : Component {

    /**
     * @param type - 0 to add an active ability, 1 to add a passive ability
     * @param ability - ability name as a string in lowercase
     * @param description - description that should explain clearly and concisely the ability
     */
    fun addAbility(
        type: Int,
        ability: String,
        description: String
    ) {
        if (type == 0) {
            activeAbilities.add(ability)
            abilityDescriptions[ability] = description
        }
        else if (type == 1) {
            passiveAbilities.add(ability)
            abilityDescriptions[ability] = description
        }
    }

    fun getActiveAbilities(): ArrayList<String> {
        return activeAbilities
    }

    fun getPassiveAbilities(): ArrayList<String> {
        return passiveAbilities
    }

    fun getAbilityDescription(
        ability: String
    ): String? {
        return abilityDescriptions[ability]
    }

    fun getAbilityTexture(
        ability: String
    ): Texture {
        return Texture(("abilities/$ability.png").toInternalFile())
    }
}
