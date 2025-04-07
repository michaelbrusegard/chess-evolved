package io.github.chessevolved.systems

import com.badlogic.gdx.graphics.Texture
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.entities.ChessPiece
import io.github.chessevolved.singletons.Mappers
import ktx.assets.toInternalFile

class AbilitySystem(
    private var abilityInventory: List<String> = listOf("explosion", "conscription", "shadow_step",
     "earthquake", "shield", "aura_of_terror", "royal_guard", "short_range_teleport", "checkers"),
    private var abilityDescriptionInventory: HashMap<String, String> = hashMapOf(
        "An explosion erupts on the chosen tile, and all adjacent tiles excluding diagonal tiles," +
            " destroying all pieces and clearing all weather events." to "explosion",
        "Summon a permanent friendly pawn in an empty adjacent empty tile." to "conscription",
        "Teleport on the other side of an adjacent piece." to "shadow_step",
        "Make a move as normal with this piece. When moving into the new chosen tile, stun all" +
            " adjacent enemy pieces for 1 turn." to "earthquake",
        "Protect a piece from harm, such as being taken by other pieces or abilities. Lasts for 2" +
            " turns or until the shield blocks an attack." to "shield",
        "Enemy pieces may not move adjacent to this piece. Enemy pieces already adjacent to this" +
            " piece are not affected. Lasts for 2 turns or until the piece is destroyed."
            to "aura_of_terror",
        "This piece cannot be taken or destroyed, as long as it is adjacent to its king piece. " +
            "Lasts for 2 turns or until the piece is destroyed." to "royal_guard",
        "At the start of your turn, this piece will randomly teleport in a adjacent tile. Lasts" +
            " for 3 turns or until the piece is destroyed." to "short_range_teleport",
        "Transform this piece into a checkers piece, making it follow checkers rules instead of" +
            " chess rules. This effect is permanent." to "checkers"
    )
) {
    /**
     * @param ability - ability name as a string in lowercase.
     * Call getAbilityInventory() to get valid ability names.
     */
    fun setAbilityToChessPiece(
        chesspiece: ChessPiece,
        ability: String
    ) {
        val abilities: AbilityComponent = Mappers.getAbilities(chesspiece)
        abilities.setAbility(ability)
    }

    //TODO: each ability could possibly be its own system
    // Abilities occur after the first round. 1 round = 2 turns, 1 turn = a player turn such as
    // white moving a piece. So 1 round happens after both players have made their first move
    /**
     * @param abilityName - A valid ability name as a string
     */
    fun ability(
        abilityName: String
    ) {
        val abilityNameLowercase = abilityName.lowercase()
        if (!abilityInventory.contains(abilityNameLowercase)) {
            throw IllegalArgumentException("Invalid ability name: '$abilityNameLowercase'.")
        }
        // Active abilities
        if (abilityNameLowercase == "explosion") {
            //TODO: implement logic for the explosion ability
            // An explosion erupts on the chosen tile, and all adjacent tiles excluding
            // diagonal tiles, destroying all pieces and clearing all weather events.
        }
        if (abilityNameLowercase == "conscription") {
            //TODO: implement logic for the conscription ability
            // Summon a permanent friendly pawn in an empty adjacent empty tile.
        }
        if (abilityNameLowercase == "shadow_step") {
            //TODO: implement logic for the shadow step ability
            // Teleport on the other side of an adjacent piece.
        }
        if (abilityNameLowercase == "earthquake") {
            //TODO: implement logic for the earthquake ability
            // Make a move as normal with this piece. When moving into the new
            // chosen tile, stun all adjacent enemy pieces for 1 turn.
        }
        // Passive abilities
        if (abilityNameLowercase == "shield") {
            //TODO: implement logic for the shield ability
            // Protect a piece from harm, such as being taken by other pieces or abilities.
            // Lasts for 2 turns or until the shield blocks an attack.
        }
        if (abilityNameLowercase == "aura_of_terror") {
            //TODO: implement logic for the aura of terror ability
            // Enemy pieces may not move adjacent to this piece. Enemy pieces already adjacent to
            // this piece are not affected. Lasts for 2 turns or until the piece is destroyed.
        }
        if (abilityNameLowercase == "royal_guard") {
            //TODO: implement logic for the royal guard ability
            // This piece cannot be taken or destroyed, as long as it is adjacent to its king piece.
            // Lasts for 2 turns or until the piece is destroyed.
        }
        if (abilityNameLowercase == "short_range_teleport") {
            //TODO: implement logic for the short range teleport ability
            // At the start of your turn, this piece will randomly teleport in a adjacent tile.
            // Lasts for 3 turns or until the piece is destroyed.
        }
        if (abilityNameLowercase == "checkers") {
            //TODO: implement logic for the checkers ability
            // Transform this piece into a checkers piece, making it follow checkers rules instead
            // of chess rules. This effect is permanent.
        }
    }

    /** @return - Entire list of active abilities */
    fun getAbilityInventory(): List<String> {
        return abilityInventory
    }

    /** @return - Entire HashMap of ability descriptions */
    fun getAbilityDescriptionInventory(): HashMap<String, String> {
        return abilityDescriptionInventory
    }

    fun getAbilityDescription(
        ability: String
    ): String? {
        return abilityDescriptionInventory[ability]
    }

    fun getAbilityTexture(
        ability: String
    ): Texture {
        return Texture(("abilities/$ability.png").toInternalFile())
    }
}
