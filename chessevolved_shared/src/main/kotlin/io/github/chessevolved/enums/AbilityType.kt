package io.github.chessevolved.enums

enum class AbilityType(
    val abilityDescription: String
) {
    SHIELD("Blocks attacks from opponent pieces"),
    EXPLOSION("Causes an explosion around the tile that your piece moves to"),
    SWAP("Swaps position with a non royal piece"),
    MIRROR("Mirrors an ability used against this piece"),
    NEW_MOVEMENT("No cluerino"),
}
