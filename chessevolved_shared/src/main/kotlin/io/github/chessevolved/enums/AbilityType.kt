package io.github.chessevolved.enums

enum class AbilityType(
    val abilityDescription: String,
    val cooldownTime: Int,
) {
    SHIELD("Blocks attacks from opponent pieces", 3),
    EXPLOSION("Causes an explosion around the tile that your piece moves to", 2),
    SWAP("Swaps position with a non royal piece", 2),
    MIRROR("Mirrors an ability used against this piece", 3),
    NEW_MOVEMENT("No cluerino", 0),
}
